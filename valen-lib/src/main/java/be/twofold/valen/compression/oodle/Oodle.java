package be.twofold.valen.compression.oodle;

import be.twofold.valen.compression.oodle.jna.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.util.*;
import com.sun.jna.*;

import java.io.*;
import java.nio.*;
import java.util.*;

final class Oodle {
    private static final OodleLibrary Oodle;
    private static final Memory decoderMemory;

    static {
        var options = Map.of(Library.OPTION_TYPE_MAPPER, new DefaultTypeMapper() {{
            addTypeConverter(NativeEnum.class, NativeEnum.converter());
        }});
        Oodle = switch (OperatingSystem.current()) {
            case Linux -> Native.load("./liboo2corelinux64.so", OodleLibrary.class, options);
            case Windows -> Native.load("oo2core_8_win64", OodleLibrary.class, options);
            case Mac -> throw new UnsupportedOperationException("Mac is not supported");
        };

        System.out.println("Loaded Oodle version: " + version());

        int memorySizeNeeded = Oodle.OodleLZDecoder_MemorySizeNeeded(OodleLZ_Compressor.Invalid, -1);
        decoderMemory = new Memory(memorySizeNeeded);
    }

    static void decompress(ByteBuffer src, ByteBuffer dst) throws IOException {
        // TODO: Fix this assumption somewhere...
        int decompressedSize = dst.remaining();

        int result = Oodle.OodleLZ_Decompress(
            src, src.remaining(), dst, dst.remaining(),
            OodleLZ_FuzzSafe.Yes, OodleLZ_CheckCRC.Yes, OodleLZ_Verbosity.None,
            Pointer.NULL, 0, Pointer.NULL, Pointer.NULL,
            decoderMemory, decoderMemory.size(), OodleLZ_Decode_ThreadPhase.All
        );

        if (result != decompressedSize) {
            throw new IOException("Decompression failed: " + result);
        }
    }

    static String version() {
        var values = new int[7];
        Oodle.Oodle_GetConfigValues(values);

        var version = values[6];
        var major = version >>> 16 & 0xff;
        var minor = version >>> 8 & 0xff;
        return "2." + major + "." + minor;
    }
}
