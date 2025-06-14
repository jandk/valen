package be.twofold.valen.game.idtech.defines;

public enum TextureFormat {
    FMT_NONE,
    FMT_RGBA32F,
    FMT_RGBA16F,
    FMT_RGBA8,
    FMT_RGBA8_SRGB,
    FMT_ARGB8,
    FMT_ALPHA,
    FMT_L8A8_DEPRECATED,
    FMT_RG8,
    FMT_LUM8_DEPRECATED,
    FMT_INT8_DEPRECATED,
    FMT_BC1,
    FMT_BC1_SRGB,
    FMT_BC1_ZERO_ALPHA,
    FMT_BC3,
    FMT_BC3_SRGB,
    FMT_BC4,
    FMT_BC5,
    FMT_BC6H_UF16,
    FMT_BC6H_SF16,
    FMT_BC7,
    FMT_BC7_SRGB,
    FMT_DEPTH,
    FMT_DEPTH_STENCIL,
    FMT_DEPTH16,
    FMT_X32F,
    FMT_Y16F_X16F,
    FMT_X16,
    FMT_Y16_X16,
    FMT_RGB565,
    FMT_R8,
    FMT_R11FG11FB10F,
    FMT_X16F,
    FMT_RG16F,
    FMT_R10G10B10A2,
    FMT_RG32F,
    FMT_R32_UINT,
    FMT_R16_UINT,
    FMT_R8_UINT,
    FMT_ASTC_4X4,
    FMT_ASTC_4X4_SRGB,
    FMT_ASTC_5X4,
    FMT_ASTC_5X4_SRGB,
    FMT_ASTC_5X5,
    FMT_ASTC_5X5_SRGB,
    FMT_ASTC_6X5,
    FMT_ASTC_6X5_SRGB,
    FMT_ASTC_6X6,
    FMT_ASTC_6X6_SRGB,
    FMT_ASTC_8X5,
    FMT_ASTC_8X5_SRGB,
    FMT_ASTC_8X6,
    FMT_ASTC_8X6_SRGB,
    FMT_ASTC_8X8,
    FMT_ASTC_8X8_SRGB,
    FMT_DEPTH32F,

    // Great Circle
    FMT_RGBA8_SNORM,
    FMT_STENCIL,
    FMT_R9G9B9E5,
    FMT_RGBA32_UINT,
    FMT_RG32_UINT,

    // Dark Ages
    FMT_SMALLF,
    FMT_MAINVIEW_SMALLF,
    FMT_RGBA16_UINT,
    FMT_RG16_UINT,
    FMT_RGBA16,
    ;

    public static TextureFormat parse(String textureFormat) {
        return switch (textureFormat.toLowerCase()) {
            case "alpha" -> TextureFormat.FMT_ALPHA;
            case "bc4" -> TextureFormat.FMT_BC4;
            case "bc5" -> TextureFormat.FMT_BC5;
            case "bc6h", "bc6huf16" -> TextureFormat.FMT_BC6H_UF16;
            case "bc6hsf16" -> TextureFormat.FMT_BC6H_SF16;
            case "bc7" -> TextureFormat.FMT_BC7;
            case "float" -> TextureFormat.FMT_RGBA16F;
            case "hqcompress", "hqcompressnormal" -> TextureFormat.FMT_BC3;
            case "r8" -> TextureFormat.FMT_R8;
            case "rg16f" -> TextureFormat.FMT_RG16F;
            case "rg32f" -> TextureFormat.FMT_RG32F;
            case "rg8" -> TextureFormat.FMT_RG8;
            case "uncompressed" -> TextureFormat.FMT_RGBA8;
            default -> null;
        };
    }
}
