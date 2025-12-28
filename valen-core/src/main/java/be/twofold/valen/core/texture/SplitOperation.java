//package be.twofold.valen.core.texture.convert;
//
//import be.twofold.valen.core.texture.*;
//import be.twofold.valen.core.util.*;
//
//import java.util.*;
//
//public final class SplitOperation {
//    private SplitOperation() {
//    }
//
//    public static Channels split(Surface source) {
//        return switch (source.format()) {
//            case R8_UNORM, R16_UNORM, R16_SFLOAT -> new Channels(source, null, null, null);
//            case R8G8_UNORM -> {
//                var split = split8(source, 2);
//                yield new Channels(split[0], split[1], null, null);
//            }
//            case R8G8B8_UNORM -> {
//                var split = split8(source, 3);
//                yield new Channels(split[0], split[1], split[2], null);
//            }
//            case R8G8B8A8_UNORM -> {
//                var split = split8(source, 4);
//                yield new Channels(split[0], split[1], split[2], split[3]);
//            }
//            case B8G8R8_UNORM -> {
//                var split = split8(source, 3);
//                yield new Channels(split[2], split[1], split[0], null);
//            }
//            case B8G8R8A8_UNORM -> {
//                var split = split8(source, 4);
//                yield new Channels(split[2], split[1], split[0], split[3]);
//            }
//            case R16G16B16A16_UNORM -> {
//                var split = split16(source, TextureFormat.R16_UNORM, 4);
//                yield new Channels(split[0], split[1], split[2], split[3]);
//            }
//            case R16G16_SFLOAT -> {
//                var split = split16(source, TextureFormat.R16_SFLOAT, 2);
//                yield new Channels(split[0], split[1], null, null);
//            }
//            case R16G16B16A16_SFLOAT -> {
//                var split = split16(source, TextureFormat.R16_SFLOAT, 4);
//                yield new Channels(split[0], split[1], split[2], split[3]);
//            }
//            default -> throw new UnsupportedOperationException("Unsupported format: " + source.format());
//        };
//    }
//
//    private static Surface[] split8(Surface surface, int channels) {
//        var source = surface.data();
//
//        var target = new byte[channels][source.length / channels];
//        for (int i = 0, o = 0; i < source.length; i += channels, o++) {
//            for (int j = 0; j < channels; j++) {
//                target[j][o] = source[i + j];
//            }
//        }
//
//        return Arrays.stream(target)
//            .map(array -> new Surface(surface.width(), surface.height(), TextureFormat.R8_UNORM, array))
//            .toArray(Surface[]::new);
//    }
//
//    private static Surface[] split16(Surface surface, TextureFormat format, int channels) {
//        var source = surface.data();
//
//        var stride = channels * 2;
//        var target = new byte[channels][source.length / channels];
//        for (int i = 0, o = 0; i < source.length; i += stride, o += 2) {
//            for (int j = 0; j < stride; j += 2) {
//                ByteArrays.setShort(target[j], o, ByteArrays.getShort(source, i + j));
//            }
//        }
//
//        return Arrays.stream(target)
//            .map(array -> new Surface(surface.width(), surface.height(), format, array))
//            .toArray(Surface[]::new);
//    }
//}