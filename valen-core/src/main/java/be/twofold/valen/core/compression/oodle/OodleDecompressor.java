package be.twofold.valen.core.compression.oodle;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.compression.oodle.jna.*;
import be.twofold.valen.core.util.*;
import com.sun.jna.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class OodleDecompressor extends Decompressor {
    private static final OodleLibrary Oodle;
    private static final Memory DecoderMemory;

    static {
        var options = Map.of(Library.OPTION_TYPE_MAPPER, new DefaultTypeMapper() {{
            addTypeConverter(NativeEnum.class, NativeEnum.converter());
        }});
        Oodle = switch (OperatingSystem.current()) {
            case Linux -> Native.load("./liboo2corelinux64.so", OodleLibrary.class, options);
            case Windows -> Native.load("oo2core_8_win64", OodleLibrary.class, options);
            case Mac -> throw new UnsupportedOperationException("Mac is not supported");
        };

        int memorySizeNeeded = Oodle.OodleLZDecoder_MemorySizeNeeded(OodleLZ_Compressor.Invalid, -1);
        DecoderMemory = new Memory(memorySizeNeeded);

        System.out.println("Loaded Oodle version: " + version());
    }

    private final boolean chunked;

    public OodleDecompressor(boolean chunked) {
        this.chunked = chunked;
    }

    private static String version() {
        var values = new int[7];
        Oodle.Oodle_GetConfigValues(values);

        var version = values[6];
        var major = (version >>> 16) & 0xff;
        var minor = (version >>> +8) & 0xff;
        return "2." + major + "." + minor;
    }

    @Override
    public ByteBuffer decompress(ByteBuffer src, int dstLength) throws IOException {
        if (chunked) {
            src = src.slice(12, src.remaining() - 12);
        }

        var dst = ByteBuffer.allocate(dstLength);
        int result = Oodle.OodleLZ_Decompress(
            src, src.remaining(), dst, dstLength,
            OodleLZ_FuzzSafe.Yes, OodleLZ_CheckCRC.Yes, OodleLZ_Verbosity.None,
            Pointer.NULL, 0, Pointer.NULL, Pointer.NULL,
            DecoderMemory, DecoderMemory.size(), OodleLZ_Decode_ThreadPhase.All
        );

        if (result != dst.remaining()) {
            throw new IOException("Decompression failed: " + result);
        }
        return dst;
    }
}
