package be.twofold.valen.game.eternal.defines;

import be.twofold.valen.core.util.*;

public enum ImageBufferFormat implements ValueEnum<Integer> {
    IBF_FLOAT(0),
    IBF_INT(1),
    IBF_UINT(2),
    IBF_RGBA32F(3),
    IBF_RGBA16F(4),
    IBF_RG32F(5),
    IBF_RG16F(6),
    IBF_R11F_G11F_B10F(7),
    IBF_R32F(8),
    IBF_R16F(9),
    IBF_RGBA16(10),
    IBF_RGB10_A2(11),
    IBF_RGBA8(12),
    IBF_RG16(13),
    IBF_RG8(14),
    IBF_R16(15),
    IBF_R8(16),
    IBF_RGBA16_SNORM(17),
    IBF_RGBA8_SNORM(18),
    IBF_RG16_SNORM(19),
    IBF_RG8_SNORM(20),
    IBF_R16_SNORM(21),
    IBF_R8_SNORM(22),
    IBF_RGBA32I(23),
    IBF_RGBA16I(24),
    IBF_RGBA8I(25),
    IBF_RG32I(26),
    IBF_RG16I(27),
    IBF_RG8I(28),
    IBF_R32I(29),
    IBF_R16I(30),
    IBF_R8I(31),
    IBF_RGBA32UI(32),
    IBF_RGBA16UI(33),
    IBF_RGB10_A2UI(34),
    IBF_RGBA8UI(35),
    IBF_RG32UI(36),
    IBF_RG16UI(37),
    IBF_RG8UI(38),
    IBF_R32UI(39),
    IBF_R16UI(40),
    IBF_R8UI(41),
    IBF_INVALID(42),
    ;

    private final int value;

    ImageBufferFormat(int value) {
        this.value = value;
    }

    public static ImageBufferFormat parse(String name) {
        return switch (name.toLowerCase()) {
            case "float" -> IBF_FLOAT;
            case "int" -> IBF_INT;
            case "uint" -> IBF_UINT;
            case "rgba32f" -> IBF_RGBA32F;
            case "rgba16f" -> IBF_RGBA16F;
            case "rg32f" -> IBF_RG32F;
            case "rg16f" -> IBF_RG16F;
            case "r11f_g11f_b10f" -> IBF_R11F_G11F_B10F;
            case "r32f" -> IBF_R32F;
            case "r16f" -> IBF_R16F;
            case "rgba16" -> IBF_RGBA16;
            case "rgb10_a2" -> IBF_RGB10_A2;
            case "rgba8" -> IBF_RGBA8;
            case "rg16" -> IBF_RG16;
            case "rg8" -> IBF_RG8;
            case "r16" -> IBF_R16;
            case "r8" -> IBF_R8;
            case "rgba16_snorm" -> IBF_RGBA16_SNORM;
            case "rgba8_snorm" -> IBF_RGBA8_SNORM;
            case "rg16_snorm" -> IBF_RG16_SNORM;
            case "rg8_snorm" -> IBF_RG8_SNORM;
            case "r16_snorm" -> IBF_R16_SNORM;
            case "r8_snorm" -> IBF_R8_SNORM;
            case "rgba32i" -> IBF_RGBA32I;
            case "rgba16i" -> IBF_RGBA16I;
            case "rgba8i" -> IBF_RGBA8I;
            case "rg32i" -> IBF_RG32I;
            case "rg16i" -> IBF_RG16I;
            case "rg8i" -> IBF_RG8I;
            case "r32i" -> IBF_R32I;
            case "r16i" -> IBF_R16I;
            case "r8i" -> IBF_R8I;
            case "rgba32ui" -> IBF_RGBA32UI;
            case "rgba16ui" -> IBF_RGBA16UI;
            case "rgb10_a2ui" -> IBF_RGB10_A2UI;
            case "rgba8ui" -> IBF_RGBA8UI;
            case "rg32ui" -> IBF_RG32UI;
            case "rg16ui" -> IBF_RG16UI;
            case "rg8ui" -> IBF_RG8UI;
            case "r32ui" -> IBF_R32UI;
            case "r16ui" -> IBF_R16UI;
            case "r8ui" -> IBF_R8UI;
            default -> throw new IllegalArgumentException("Unknown image buffer format: " + name);
        };
    }

    @Override
    public Integer value() {
        return value;
    }
}
