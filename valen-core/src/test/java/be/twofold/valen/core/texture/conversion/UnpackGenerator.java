package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.texture.*;

import java.util.*;
import java.util.stream.*;

class UnpackGenerator {
    public static void main(String[] args) {
        var formats = Arrays.stream(TextureFormat.values())
            .filter(f -> !f.isCompressed())
            .toList();

        var pairs = new LinkedHashMap<TextureFormat, List<TextureFormat>>();
        for (var srcFormat : formats) {
            for (var dstFormat : formats) {
                if (srcFormat == dstFormat ||
                    srcFormat.interp() != dstFormat.interp() ||
                    srcFormat.block().size() >= dstFormat.block().size() ||
                    bytesPerChannel(srcFormat) != bytesPerChannel(dstFormat)
                ) {
                    continue;
                }
                pairs.computeIfAbsent(srcFormat, _ -> new ArrayList<>()).add(dstFormat);
            }
        }

        System.out.println("return switch (srcFormat) {");
        for (var entry : pairs.entrySet()) {
            var srcFormat = entry.getKey();
            System.out.println("    case " + srcFormat + " -> switch (dstFormat) {");
            for (var dstFormat : entry.getValue())
                System.out.println("        case " + dstFormat + " -> Unpack::" + methodName(srcFormat, dstFormat) + ";");
            System.out.println("        default -> null;");
            System.out.println("    };");
        }
        System.out.println("    default -> null;");
        System.out.println("};");
        System.out.println();
        System.out.println();

        for (var entry : pairs.entrySet()) {
            var srcFormat = entry.getKey();
            for (var dstFormat : entry.getValue()) {
                generate(srcFormat, dstFormat);
            }
        }

//        generate(TextureFormat.R8G8B8_UNORM, 1, TextureFormat.B8G8R8A8_UNORM, 1);
    }

    private static int bytesPerChannel(TextureFormat format) {
        return format.block().size() / formatToChannels(format).size();
    }

    private static void generate(TextureFormat srcFormat, TextureFormat dstFormat) {
        var srcStride = srcFormat.block().size();
        var dstStride = dstFormat.block().size();
        var singleStride = srcStride == dstStride;

        var incrementI = "i" + increment(srcStride);
        var incrementO = "o" + increment(dstStride);

        System.out.println("private static void " + methodName(srcFormat, dstFormat) + "(byte[] src, byte[] dst) {");
        System.out.println("    for (int i = 0" + (singleStride ? "" : ", o = 0") + "; i < src.length; " + incrementI + (singleStride ? "" : ", " + incrementO) + ") {");

        var srcChannelSize = bytesPerChannel(srcFormat);
        var dstChannelSize = bytesPerChannel(dstFormat);
        var srcChannels = formatToChannels(srcFormat);
        var dstChannels = formatToChannels(dstFormat);
        for (int dstIndex = 0; dstIndex < dstChannels.size(); dstIndex++) {
            var dstChannel = dstChannels.get(dstIndex);
            var srcIndexOpt = IntStream.range(0, srcChannels.size())
                .filter(i1 -> srcChannels.get(i1) == dstChannel)
                .findFirst();

            if (srcIndexOpt.isEmpty()) {
                if (dstChannel != Channel.A) {
                    continue;
                }

                var fill = fill(dstChannelSize, dstFormat.interp());
                for (var i = 0; i < srcChannelSize; i++) {
                    if (fill[i] == 0) {
                        continue;
                    }
                    var dstOffset = dstIndex * dstChannelSize + i;
                    var filler = String.format("(byte) 0x%02X", fill[i]);
                    System.out.println("        dst[" + (singleStride ? "i" : "o") + offset(dstOffset) + "] = " + filler + ";");
                }
                continue;
            }

            var srcIndex = srcIndexOpt.getAsInt();
            for (var i = 0; i < srcChannelSize; i++) {
                var srcOffset = srcIndex * srcChannelSize + i;
                var dstOffset = dstIndex * dstChannelSize + i;
                System.out.println("        dst[" + (singleStride ? "i" : "o") + offset(dstOffset) + "] = src[i" + offset(srcOffset) + "];");
            }
        }

        System.out.println("    }");
        System.out.println("}");
        System.out.println();
    }

    private static String methodName(TextureFormat srcFormat, TextureFormat dstFormat) {
        return "unpack" + name(srcFormat) + "To" + name(dstFormat);
    }

    private static String name(TextureFormat format) {
        var s = format.toString();
        var index = s.lastIndexOf('_');
        return s.substring(0, index) + s.charAt(index + 1) + s.substring(index + 2).toLowerCase();
    }

    private static byte[] fill(int channelSize, TextureFormat.Interp interp) {
        return switch (channelSize) {
            case 1 -> switch (interp) {
                case SNorm -> new byte[]{0x7F};
                case UNorm, SRGB -> new byte[]{(byte) 0xFF};
                default -> throw new IllegalArgumentException();
            };
            case 2 -> switch (interp) {
                case SFloat -> new byte[]{0x00, 0x3C};
                case SNorm -> new byte[]{(byte) 0xFF, 0x7F};
                case UNorm, SRGB -> new byte[]{(byte) 0xFF, (byte) 0xFF};
                default -> throw new IllegalArgumentException();
            };
            default -> throw new IllegalArgumentException();
        };
    }

    private static String offset(int offset) {
        return offset == 0 ? "/**/" : " + " + offset;
    }

    private static String increment(int stride) {
        if (stride == 1) {
            return "++";
        }
        return " += " + stride;
    }

    private static List<Channel> formatToChannels(TextureFormat format) {
        return switch (format) {
            case R8_UNORM,
                 R16_UNORM,
                 R16_SFLOAT -> List.of(Channel.R);
            case R8G8_UNORM,
                 R16G16_SFLOAT -> List.of(Channel.R, Channel.G);
            case R8G8B8_UNORM,
                 R16G16B16_SFLOAT -> List.of(Channel.R, Channel.G, Channel.B);
            case R8G8B8A8_UNORM,
                 R16G16B16A16_UNORM,
                 R16G16B16A16_SFLOAT -> List.of(Channel.R, Channel.G, Channel.B, Channel.A);
            case B8G8R8_UNORM -> List.of(Channel.B, Channel.G, Channel.R);
            case B8G8R8A8_UNORM -> List.of(Channel.B, Channel.G, Channel.R, Channel.A);
            default -> throw new UnsupportedOperationException();
        };
    }

    private enum Channel {
        R, G, B, A;
    }
}
