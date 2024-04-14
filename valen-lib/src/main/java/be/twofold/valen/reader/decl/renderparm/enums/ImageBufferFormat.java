package be.twofold.valen.reader.decl.renderparm.enums;

public enum ImageBufferFormat {
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
    IBF_INVALID(42);

    private final int value;

    ImageBufferFormat(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
