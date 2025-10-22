package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.texture.*;
import com.squareup.javapoet.*;

import javax.lang.model.element.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

final class UnpackGenerator {
    private static final String CLASS_NAME = "Unpackers";

    public static void main(String[] args) throws IOException {
        var formats = Arrays.stream(TextureFormat.values())
            .filter(f -> !f.isCompressed())
            .toList();

        var pairs = new LinkedHashMap<TextureFormat, List<TextureFormat>>();
        for (var srcFormat : formats) {
            for (var dstFormat : formats) {
                if (srcFormat == dstFormat ||
                    formatToInterp(srcFormat) != formatToInterp(dstFormat) ||
                    srcFormat.blockSize() >= dstFormat.blockSize() ||
                    bytesPerChannel(srcFormat) != bytesPerChannel(dstFormat)
                ) {
                    continue;
                }
                pairs.computeIfAbsent(srcFormat, _ -> new ArrayList<>()).add(dstFormat);
            }
        }

        // Generate the switch statement method
        var switchMethod = generateGetConverter(pairs);

        // Generate all conversion methods
        List<MethodSpec> conversionMethods = new ArrayList<>();
        for (var entry : pairs.entrySet()) {
            var srcFormat = entry.getKey();
            for (var dstFormat : entry.getValue()) {
                conversionMethods.add(generateConversionMethod(srcFormat, dstFormat));
            }
        }

        // Create the class
        var unpackClass = TypeSpec.classBuilder(CLASS_NAME)
            .addModifiers(Modifier.FINAL)
            .addMethod(switchMethod)
            .addMethods(conversionMethods)
            .build();

        // Write to file
        JavaFile
            .builder("be.twofold.valen.core.texture.conversion", unpackClass)
            .indent("    ")
            .build()
            .writeTo(Path.of("valen-core/src/main/java"));
    }

    private static MethodSpec generateGetConverter(LinkedHashMap<TextureFormat, List<TextureFormat>> pairs) {
        var builder = CodeBlock.builder()
            .beginControlFlow("switch (srcFormat)");

        for (var entry : pairs.entrySet()) {
            builder.beginControlFlow("case $L:", entry.getKey());
            generateGetConverterInnerSwitch(builder, entry.getKey(), entry.getValue());
            builder.endControlFlow();
        }

        builder
            .endControlFlow()
            .addStatement("return null");

        return MethodSpec.methodBuilder("getConverter")
            .addModifiers(Modifier.STATIC)
            .returns(ParameterizedTypeName.get(BiConsumer.class, byte[].class, byte[].class))
            .addParameter(TextureFormat.class, "srcFormat")
            .addParameter(TextureFormat.class, "dstFormat")
            .addCode(builder.build())
            .build();
    }

    private static void generateGetConverterInnerSwitch(CodeBlock.Builder builder, TextureFormat srcFormat, List<TextureFormat> dstFormats) {
        builder.beginControlFlow("switch (dstFormat)");

        for (var dstFormat : dstFormats) {
            builder
                .beginControlFlow("case $L:", dstFormat)
                .addStatement("return $T::$L", ClassName.get("", CLASS_NAME), methodName(srcFormat, dstFormat))
                .endControlFlow();
        }

        builder
            .beginControlFlow("default:")
            .addStatement("break")
            .endControlFlow();

        builder.endControlFlow();
    }

    private static MethodSpec generateConversionMethod(TextureFormat srcFormat, TextureFormat dstFormat) {
        var srcStride = srcFormat.blockSize();
        var dstStride = dstFormat.blockSize();
        var singleStride = srcStride == dstStride;

        var methodCode = CodeBlock.builder();
        // Generate for loop
        if (singleStride) {
            methodCode.beginControlFlow("for (int i = 0; i < src.length; i$L)", increment(srcStride));
        } else {
            methodCode.beginControlFlow("for (int i = 0, o = 0; i < src.length; i$L, o$L)", increment(srcStride), increment(dstStride));
        }

        // Generate channel copying logic
        var srcChannelSize = bytesPerChannel(srcFormat);
        var dstChannelSize = bytesPerChannel(dstFormat);
        var srcChannels = formatToChannels(srcFormat);
        var dstChannels = formatToChannels(dstFormat);

        for (var dstIndex = 0; dstIndex < dstChannels.size(); dstIndex++) {
            var dstChannel = dstChannels.get(dstIndex);
            var srcIndexOpt = IntStream.range(0, srcChannels.size())
                .filter(i1 -> srcChannels.get(i1) == dstChannel)
                .findFirst();

            if (srcIndexOpt.isEmpty()) {
                if (dstChannel != Channel.A) {
                    continue;
                }

                var fill = fill(dstChannelSize, formatToInterp(dstFormat));
                for (var i = 0; i < srcChannelSize; i++) {
                    if (fill[i] == 0) {
                        continue;
                    }
                    var dstOffset = dstIndex * dstChannelSize + i;
                    var filler = String.format("(byte) 0x%02X", fill[i]);
                    var indexExpr = singleStride ? "i" : "o";
                    var offsetExpr = offset(dstOffset);
                    methodCode.addStatement("dst[$L$L] = $L", indexExpr, offsetExpr, filler);
                }
                continue;
            }

            var srcIndex = srcIndexOpt.getAsInt();
            for (var i = 0; i < srcChannelSize; i++) {
                var srcOffset = srcIndex * srcChannelSize + i;
                var dstOffset = dstIndex * dstChannelSize + i;
                var dstIndexExpr = singleStride ? "i" : "o";
                var dstOffsetExpr = offset(dstOffset);
                var srcOffsetExpr = offset(srcOffset);
                methodCode.addStatement("dst[$L$L] = src[i$L]", dstIndexExpr, dstOffsetExpr, srcOffsetExpr);
            }
        }

        methodCode.endControlFlow();

        return MethodSpec.methodBuilder(methodName(srcFormat, dstFormat))
            .addModifiers(Modifier.PRIVATE, Modifier.STATIC)
            .returns(TypeName.VOID)
            .addParameter(byte[].class, "src")
            .addParameter(byte[].class, "dst")
            .addCode(methodCode.build())
            .build();
    }

    private static int bytesPerChannel(TextureFormat format) {
        return format.blockSize() / formatToChannels(format).size();
    }

    private static String methodName(TextureFormat srcFormat, TextureFormat dstFormat) {
        return "unpack" + name(srcFormat) + "To" + name(dstFormat);
    }

    private static String name(TextureFormat format) {
        var s = format.toString();
        var index = s.lastIndexOf('_');
        return s.substring(0, index) + s.charAt(index + 1) + s.substring(index + 2).toLowerCase();
    }

    private static byte[] fill(int channelSize, Interp interp) {
        return switch (channelSize) {
            case 1 -> switch (interp) {
                case SNorm -> new byte[]{0x7F};
                case UNorm, SRGB -> new byte[]{(byte) 0xFF};
                default -> throw new UnsupportedOperationException();
            };
            case 2 -> switch (interp) {
                case SFloat, UFloat -> new byte[]{0x00, 0x3C};
                case SNorm -> new byte[]{(byte) 0xFF, 0x7F};
                case UNorm, SRGB -> new byte[]{(byte) 0xFF, (byte) 0xFF};
            };
            default -> throw new UnsupportedOperationException();
        };
    }

    private static String offset(int offset) {
        return offset == 0 ? "/**/" : " + " + offset;
    }

    private static String increment(int stride) {
        return stride == 1 ? "++" : " += " + stride;
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

    private static Interp formatToInterp(TextureFormat format) {
        return switch (format) {
            case R8_UNORM,
                 R8G8_UNORM,
                 R8G8B8_UNORM,
                 R8G8B8A8_UNORM,
                 B8G8R8_UNORM,
                 B8G8R8A8_UNORM,
                 R16_UNORM,
                 R16G16B16A16_UNORM,
                 BC1_UNORM,
                 BC2_UNORM,
                 BC3_UNORM,
                 BC4_UNORM,
                 BC5_UNORM,
                 BC7_UNORM -> Interp.UNorm;
            case R16_SFLOAT,
                 R16G16_SFLOAT,
                 R16G16B16_SFLOAT,
                 R16G16B16A16_SFLOAT,
                 BC6H_SFLOAT -> Interp.SFloat;
            case BC1_SRGB,
                 BC2_SRGB,
                 BC3_SRGB,
                 BC7_SRGB -> Interp.SRGB;
            case R8_SNORM,
                 R8G8_SNORM,
                 R8G8B8A8_SNORM,
                 R16_SNORM,
                 BC4_SNORM,
                 BC5_SNORM -> Interp.SNorm;
            case BC6H_UFLOAT -> Interp.UFloat;
        };
    }

    private enum Channel {
        R,
        G,
        B,
        A,
    }

    private enum Interp {
        SFloat,
        SNorm,
        SRGB,
        UFloat,
        UNorm,
    }
}
