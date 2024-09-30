package be.twofold.valen.core.compression.oodle;

import be.twofold.valen.core.compression.jna.*;
import be.twofold.valen.core.util.*;
import com.sun.jna.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class Oodle {
    private static volatile Oodle INSTANCE;
    private final OodleLibrary library;
    private final Memory memory;

    private Oodle() {
        var options = Map.of(Library.OPTION_TYPE_MAPPER, new DefaultTypeMapper() {{
            addTypeConverter(NativeEnum.class, NativeEnum.converter());
        }});
        this.library = switch (OperatingSystem.current()) {
            case Linux -> Native.load("./liboo2corelinux64.so", OodleLibrary.class, options);
            case Windows -> Native.load("oo2core_8_win64", OodleLibrary.class, options);
            case Mac -> throw new UnsupportedOperationException("Mac is not supported");
        };

        var memorySizeNeeded = library.OodleLZDecoder_MemorySizeNeeded(OodleLZ_Compressor.Invalid, -1);
        this.memory = new Memory(memorySizeNeeded);

        System.out.println("Loaded Oodle version: " + version());
    }

    public static Oodle instance() {
        if (INSTANCE == null) {
            synchronized (Oodle.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Oodle();
                }
            }
        }
        return INSTANCE;
    }

    private String version() {
        var values = new int[7];
        library.Oodle_GetConfigValues(values);

        var version = values[6];
        var major = (version >>> 16) & 0xff;
        var minor = (version >>> +8) & 0xff;
        return "2." + major + "." + minor;
    }

    public void decompress(ByteBuffer src, ByteBuffer dst) throws IOException {
        var result = library.OodleLZ_Decompress(
            src, src.remaining(), dst, dst.remaining(),
            OodleLZ_FuzzSafe.Yes, OodleLZ_CheckCRC.Yes, OodleLZ_Verbosity.None,
            Pointer.NULL, 0, Pointer.NULL, Pointer.NULL,
            memory, memory.size(), OodleLZ_Decode_ThreadPhase.All
        );

        if (result != dst.remaining()) {
            throw new IOException("Decompression failed: " + result);
        }
    }
}
