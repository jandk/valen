//package be.twofold.valen.core.compression.ooz.ffm;
//
//import java.lang.foreign.*;
//import java.lang.invoke.*;
//
//public class OozFFM {
//    private static final MethodHandle KRAKEN_DECOMPRESS;
//
//    static {
//        var lookup = SymbolLookup.libraryLookup("ooz", Arena.global());
//        var segment = lookup.find("Kraken_Decompress")
//            .orElseThrow(() -> new IllegalStateException("Kraken_Decompress not found"));
//
//        KRAKEN_DECOMPRESS = Linker.nativeLinker().downcallHandle(segment, FunctionDescriptor.of(
//            ValueLayout.JAVA_INT,
//            ValueLayout.ADDRESS,
//            ValueLayout.JAVA_INT,
//            ValueLayout.ADDRESS,
//            ValueLayout.JAVA_INT
//        ));
//    }
//
//    public static int Kraken_Decompress(byte[] src, int srcLength, byte[] dst, int dstLength) throws Throwable {
//        try (var arena = Arena.ofConfined()) {
//            var srcSegment = MemorySegment.ofArray(src);
//            var dstSegment = arena.allocate(ValueLayout.JAVA_BYTE, dstLength + 64);
//            var result = (int) KRAKEN_DECOMPRESS.invokeExact(srcSegment, srcLength, dstSegment, dstLength);
//            if (result != dstLength) {
//                throw new IllegalStateException("Error decompressing data");
//            }
//
//            byte[] dstArray = dstSegment.toArray(ValueLayout.JAVA_BYTE);
//            System.arraycopy(dstArray, 0, dst, 0, dstLength);
//        }
//        return 0;
//    }
//}
