package be.twofold.valen.game.greatcircle.reader.image;

import be.twofold.valen.core.util.*;

public enum ImageTextureFormat implements ValueEnum<Integer> {
    FMT_NONE(0),
    FMT_RGBA32F(1),
    FMT_RGBA16F(2),
    FMT_RGBA8(3),
    FMT_RGBA8_SRGB(32),
    FMT_RGBA8_SNORM(60),
    FMT_ARGB8(4),
    FMT_ALPHA(5),
    FMT_L8A8_DEPRECATED(6),
    FMT_RG8(7),
    FMT_LUM8_DEPRECATED(8),
    FMT_INT8_DEPRECATED(9),
    FMT_BC1(10),
    FMT_BC1_SRGB(33),
    FMT_BC1_ZERO_ALPHA(54),
    FMT_BC3(11),
    FMT_BC3_SRGB(34),
    FMT_BC4(24),
    FMT_BC5(25),
    FMT_BC6H_UF16(22),
    FMT_BC6H_SF16(36),
    FMT_BC7(23),
    FMT_BC7_SRGB(35),
    FMT_DEPTH(12),
    FMT_DEPTH_STENCIL(13),
    FMT_DEPTH16(31),
    FMT_STENCIL(59),
    FMT_X32F(14),
    FMT_Y16F_X16F(15),
    FMT_X16(16),
    FMT_Y16_X16(17),
    FMT_RGB565(18),
    FMT_R8(19),
    FMT_R11FG11FB10F(20),
    FMT_R9G9B9E5(56),
    FMT_X16F(21),
    FMT_RG16F(26),
    FMT_R10G10B10A2(27),
    FMT_RG32F(28),
    FMT_R32_UINT(29),
    FMT_R16_UINT(30),
    FMT_R8_UINT(55),
    FMT_RGBA32_UINT(57),
    FMT_RG32_UINT(58),
    FMT_ASTC_4X4(37),
    FMT_ASTC_4X4_SRGB(38),
    FMT_ASTC_5X4(39),
    FMT_ASTC_5X4_SRGB(40),
    FMT_ASTC_5X5(41),
    FMT_ASTC_5X5_SRGB(42),
    FMT_ASTC_6X5(43),
    FMT_ASTC_6X5_SRGB(44),
    FMT_ASTC_6X6(45),
    FMT_ASTC_6X6_SRGB(46),
    FMT_ASTC_8X5(47),
    FMT_ASTC_8X5_SRGB(48),
    FMT_ASTC_8X6(49),
    FMT_ASTC_8X6_SRGB(50),
    FMT_ASTC_8X8(51),
    FMT_ASTC_8X8_SRGB(52),
    FMT_DEPTH32F(53),
    FMT_NEXTAVAILABLE(61);

    private final int value;

    ImageTextureFormat(int value) {
        this.value = value;
    }

    public static ImageTextureFormat fromValue(int value) {
        return ValueEnum.fromValue(ImageTextureFormat.class, value);
    }

    @Override
    public Integer value() {
        return value;
    }
}
