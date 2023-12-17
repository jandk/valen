package be.twofold.valen.reader.image;

import java.util.*;

public enum ImageTextureFormat {
    FMT_NONE(0x00),
    FMT_RGBA32F(0x01),
    FMT_RGBA16F(0x02),
    FMT_RGBA8(0x03),
    FMT_ARGB8(0x04),
    FMT_ALPHA(0x05),
    FMT_L8A8_DEPRECATED(0x06),
    FMT_RG8(0x07),
    FMT_LUM8_DEPRECATED(0x08),
    FMT_INT8_DEPRECATED(0x09),
    FMT_BC1(0x0a),
    FMT_BC3(0x0b),
    FMT_DEPTH(0x0c),
    FMT_DEPTH_STENCIL(0x0d),
    FMT_X32F(0x0e),
    FMT_Y16F_X16F(0x0f),
    FMT_X16(0x10),
    FMT_Y16_X16(0x11),
    FMT_RGB565(0x12),
    FMT_R8(0x13),
    FMT_R11FG11FB10F(0x14),
    FMT_X16F(0x15),
    FMT_BC6H_UF16(0x16),
    FMT_BC7(0x17),
    FMT_BC4(0x18),
    FMT_BC5(0x19),
    FMT_RG16F(0x1a),
    FMT_R10G10B10A2(0x1b),
    FMT_RG32F(0x1c),
    FMT_R32_UINT(0x1d),
    FMT_R16_UINT(0x1e),
    FMT_DEPTH16(0x1f),
    FMT_RGBA8_SRGB(0x20),
    FMT_BC1_SRGB(0x21),
    FMT_BC3_SRGB(0x22),
    FMT_BC7_SRGB(0x23),
    FMT_BC6H_SF16(0x24),
    FMT_ASTC_4X4(0x25),
    FMT_ASTC_4X4_SRGB(0x26),
    FMT_ASTC_5X4(0x27),
    FMT_ASTC_5X4_SRGB(0x28),
    FMT_ASTC_5X5(0x29),
    FMT_ASTC_5X5_SRGB(0x2a),
    FMT_ASTC_6X5(0x2b),
    FMT_ASTC_6X5_SRGB(0x2c),
    FMT_ASTC_6X6(0x2d),
    FMT_ASTC_6X6_SRGB(0x2e),
    FMT_ASTC_8X5(0x2f),
    FMT_ASTC_8X5_SRGB(0x30),
    FMT_ASTC_8X6(0x31),
    FMT_ASTC_8X6_SRGB(0x32),
    FMT_ASTC_8X8(0x33),
    FMT_ASTC_8X8_SRGB(0x34),
    FMT_DEPTH32F(0x35),
    FMT_BC1_ZERO_ALPHA(0x36),
    FMT_NEXTAVAILABLE(0x37);

    private static final ImageTextureFormat[] VALUES = values();
    private final int code;

    ImageTextureFormat(int code) {
        this.code = code;
    }

    public static ImageTextureFormat fromCode(int code) {
        return Arrays.stream(VALUES)
            .filter(value -> value.code == code)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown texture format: " + code));
    }
}
