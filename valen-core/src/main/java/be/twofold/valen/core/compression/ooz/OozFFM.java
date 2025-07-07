package be.twofold.valen.core.compression.ooz;

import java.lang.foreign.*;
import java.lang.invoke.*;
import java.nio.file.*;

public final class OozFFM {
    private final SymbolLookup lookup;

    private final MethodHandle Kraken_Compress;
    private final MethodHandle Kraken_Decompress;

    public OozFFM(Path path, Arena arena) {
        lookup = SymbolLookup.libraryLookup(path, arena);

        this.Kraken_Compress = lookup("Kraken_Compress", FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS,
                ValueLayout.JAVA_LONG,
                ValueLayout.ADDRESS,
                ValueLayout.JAVA_INT
            )
        );
        this.Kraken_Decompress = lookup("Kraken_Decompress", FunctionDescriptor.of(
                ValueLayout.JAVA_INT,
                ValueLayout.ADDRESS,
                ValueLayout.JAVA_LONG,
                ValueLayout.ADDRESS,
                ValueLayout.JAVA_LONG
            )
        );
    }

    private MethodHandle lookup(String methodName, FunctionDescriptor methodDescriptor) {
        var address = lookup.find(methodName)
            .orElseThrow(() -> new UnsatisfiedLinkError("Unresolved symbol: " + methodName));
        return Linker.nativeLinker().downcallHandle(address, methodDescriptor);
    }

    public int Kraken_Compress(MemorySegment src, long srcLen, MemorySegment dst, int level) {
        try {
            return (int) Kraken_Compress.invokeExact(src, srcLen, dst, level);
        } catch (Throwable e) {
            throw new AssertionError("should not reach here", e);
        }
    }

    public int Kraken_Decompress(MemorySegment src, long srcLen, MemorySegment dst, long dstLen) {
        try {
            return (int) Kraken_Decompress.invokeExact(src, srcLen, dst, dstLen);
        } catch (Throwable e) {
            throw new AssertionError("should not reach here", e);
        }
    }
}
