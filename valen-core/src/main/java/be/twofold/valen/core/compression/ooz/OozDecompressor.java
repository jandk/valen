//package be.twofold.valen.core.compression.ooz;
//
//import be.twofold.valen.core.compression.*;
//import be.twofold.valen.core.compression.ooz.jna.*;
//import be.twofold.valen.core.util.*;
//import com.sun.jna.*;
//
//import java.io.*;
//import java.nio.*;
//import java.util.*;
//
//public final class OozDecompressor extends Decompressor {
//    private static final OozLibrary Ooz;
//
//    static {
//        var options = Map.of(Library.OPTION_TYPE_MAPPER, new DefaultTypeMapper() {{
//            addTypeConverter(NativeEnum.class, NativeEnum.converter());
//        }});
//        Ooz = switch (OperatingSystem.current()) {
//            case Linux -> Native.load("./liboo2corelinux64.so", OozLibrary.class, options);
//            case Windows -> Native.load("ooz", OozLibrary.class, options);
//            case Mac -> throw new UnsupportedOperationException("Mac is not supported");
//        };
//    }
//
//    @Override
//    public ByteBuffer decompress(ByteBuffer src, int dstLength) throws IOException {
//        var dst = ByteBuffer.allocate(dstLength + 64);
//        var result = Ooz.Kraken_Decompress(src, src.remaining(), dst, dstLength);
//
//        if (result != dstLength) {
//            throw new IOException("Decompression failed: " + result);
//        }
//        return dst.limit(dst.limit() - 64);
//    }
//}
