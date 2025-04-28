package be.twofold.valen.game.greatcircle.reader.decl.renderparm;

public enum ImageBufferFormat {
    IBF_FLOAT(0),
    IBF_INT(1),
    IBF_UINT(2),
    IBF_RGBA32F(3),
    IBF_RGBA16F(4),
    IBF_RG32F(5),
    IBF_RG16F(6),
    IBF_R11F_G11F_B10F(7),
    IBF_RGB9E5(8),
    IBF_R32F(9),
    IBF_R16F(10),
    IBF_RGBA16(11),
    IBF_RGB10_A2(12),
    IBF_RGBA8(13),
    IBF_RG16(14),
    IBF_RG8(15),
    IBF_R16(16),
    IBF_R8(17),
    IBF_RGBA16_SNORM(18),
    IBF_RGBA8_SNORM(19),
    IBF_RG16_SNORM(20),
    IBF_RG8_SNORM(21),
    IBF_R16_SNORM(22),
    IBF_R8_SNORM(23),
    IBF_RGBA16F_HALF(24),
    IBF_RGBA8_SNORM_HALF(25),
    IBF_RGBA32I(26),
    IBF_RGBA16I(27),
    IBF_RGBA8I(28),
    IBF_RG32I(29),
    IBF_RG16I(30),
    IBF_RG8I(31),
    IBF_R32I(32),
    IBF_R16I(33),
    IBF_R8I(34),
    IBF_RGBA32UI(35),
    IBF_RGBA16UI(36),
    IBF_RGB10_A2UI(37),
    IBF_RGBA8UI(38),
    IBF_RG32UI(39),
    IBF_RG16UI(40),
    IBF_RG8UI(41),
    IBF_R32UI(42),
    IBF_R16UI(43),
    IBF_R8UI(44),
    IBF_INVALID(45),
    ;

    private final int value;

    ImageBufferFormat(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
