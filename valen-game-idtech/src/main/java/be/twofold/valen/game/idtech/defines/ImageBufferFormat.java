package be.twofold.valen.game.idtech.defines;

public enum ImageBufferFormat {
    IBF_FLOAT,
    IBF_INT,
    IBF_UINT,
    IBF_RGBA32F,
    IBF_RGBA16F,
    IBF_RG32F,
    IBF_RG16F,
    IBF_R11F_G11F_B10F,
    IBF_RGB9E5,
    IBF_R32F,
    IBF_R16F,
    IBF_RGBA16,
    IBF_RGB10_A2,
    IBF_RGBA8,
    IBF_RG16,
    IBF_RG8,
    IBF_R16,
    IBF_R8,
    IBF_RGBA16_SNORM,
    IBF_RGBA8_SNORM,
    IBF_RG16_SNORM,
    IBF_RG8_SNORM,
    IBF_R16_SNORM,
    IBF_R8_SNORM,
    IBF_RGBA16F_HALF,
    IBF_RGBA8_SNORM_HALF,
    IBF_RGBA32I,
    IBF_RGBA16I,
    IBF_RGBA8I,
    IBF_RG32I,
    IBF_RG16I,
    IBF_RG8I,
    IBF_R32I,
    IBF_R16I,
    IBF_R8I,
    IBF_RGBA32UI,
    IBF_RGBA16UI,
    IBF_RGB10_A2UI,
    IBF_RGBA8UI,
    IBF_RG32UI,
    IBF_RG16UI,
    IBF_RG8UI,
    IBF_R32UI,
    IBF_R16UI,
    IBF_R8UI,
    IBF_INVALID,
    ;

    public static ImageBufferFormat parse(String format) {
        return switch (format.toLowerCase()) {
            case "float" -> ImageBufferFormat.IBF_FLOAT;
            case "uint" -> ImageBufferFormat.IBF_UINT;
            case "int" -> ImageBufferFormat.IBF_INT;
            case "rgba32f" -> ImageBufferFormat.IBF_RGBA32F;
            case "rgba16f" -> ImageBufferFormat.IBF_RGBA16F;
            case "rgba16f_half" -> ImageBufferFormat.IBF_RGBA16F_HALF;
            case "rg32f" -> ImageBufferFormat.IBF_RG32F;
            case "rg16f" -> ImageBufferFormat.IBF_RG16F;
            case "r11f_g11f_b10f" -> ImageBufferFormat.IBF_R11F_G11F_B10F;
            case "r32f" -> ImageBufferFormat.IBF_R32F;
            case "r16f" -> ImageBufferFormat.IBF_R16F;
            case "rgba32ui" -> ImageBufferFormat.IBF_RGBA32UI;
            case "rgba16ui" -> ImageBufferFormat.IBF_RGBA16UI;
            case "rgb10_a2ui" -> ImageBufferFormat.IBF_RGB10_A2UI;
            case "rgba8ui" -> ImageBufferFormat.IBF_RGBA8UI;
            case "rg32ui" -> ImageBufferFormat.IBF_RG32UI;
            case "rg16ui" -> ImageBufferFormat.IBF_RG16UI;
            case "rg8ui" -> ImageBufferFormat.IBF_RG8UI;
            case "r32ui" -> ImageBufferFormat.IBF_R32UI;
            case "r16ui" -> ImageBufferFormat.IBF_R16UI;
            case "r8ui" -> ImageBufferFormat.IBF_R8UI;
            case "rgba32i" -> ImageBufferFormat.IBF_RGBA32I;
            case "rgba16i" -> ImageBufferFormat.IBF_RGBA16I;
            case "rgba8i" -> ImageBufferFormat.IBF_RGBA8I;
            case "rg32i" -> ImageBufferFormat.IBF_RG32I;
            case "rg16i" -> ImageBufferFormat.IBF_RG16I;
            case "rg8i" -> ImageBufferFormat.IBF_RG8I;
            case "r32i" -> ImageBufferFormat.IBF_R32I;
            case "r16i" -> ImageBufferFormat.IBF_R16I;
            case "r8i" -> ImageBufferFormat.IBF_R8I;
            case "rgba16" -> ImageBufferFormat.IBF_RGBA16;
            case "rgb10_a2" -> ImageBufferFormat.IBF_RGB10_A2;
            case "rgba8" -> ImageBufferFormat.IBF_RGBA8;
            case "rg16" -> ImageBufferFormat.IBF_RG16;
            case "rg8" -> ImageBufferFormat.IBF_RG8;
            case "r16" -> ImageBufferFormat.IBF_R16;
            case "r8" -> ImageBufferFormat.IBF_R8;
            case "rgba16_snorm" -> ImageBufferFormat.IBF_RGBA16_SNORM;
            case "rgba8_snorm" -> ImageBufferFormat.IBF_RGBA8_SNORM;
            case "rgba8_snorm_half" -> ImageBufferFormat.IBF_RGBA8_SNORM_HALF;
            case "rg16_snorm" -> ImageBufferFormat.IBF_RG16_SNORM;
            case "rg8_snorm" -> ImageBufferFormat.IBF_RG8_SNORM;
            case "r16_snorm" -> ImageBufferFormat.IBF_R16_SNORM;
            case "r8_snorm" -> ImageBufferFormat.IBF_R8_SNORM;
            default -> throw new IllegalArgumentException("Unknown image buffer format: " + format);
        };
    }
}
