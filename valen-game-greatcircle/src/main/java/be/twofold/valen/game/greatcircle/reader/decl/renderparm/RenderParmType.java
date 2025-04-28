package be.twofold.valen.game.greatcircle.reader.decl.renderparm;

public enum RenderParmType {
    PT_F32_VEC4(0),
    PT_F32_VEC3(1),
    PT_F32_VEC2(2),
    PT_F32(3),
    PT_F16_VEC4(4),
    PT_F16_VEC3(5),
    PT_F16_VEC2(6),
    PT_F16(7),
    PT_UI32(8),
    PT_SI32(9),
    PT_BOOL(10),
    PT_TEXTURE_2D(11),
    PT_TEXTURE_2D_HALF(12),
    PT_TEXTURE_2D_UI(13),
    PT_TEXTURE_3D(14),
    PT_TEXTURE_CUBE(15),
    PT_TEXTURE_ARRAY_2D(16),
    PT_TEXTURE_ARRAY_CUBE(17),
    PT_TEXTURE_MULTISAMPLE_2D(18),
    PT_TEXTURE_STENCIL(19),
    PT_TEXTURE_SHADOW_2D(20),
    PT_TEXTURE_SHADOW_3D(21),
    PT_TEXTURE_SHADOW_CUBE(22),
    PT_SAMPLER(23),
    PT_SAMPLER_SHADOW_2D(24),
    PT_SAMPLER_SHADOW_3D(25),
    PT_SAMPLER_SHADOW_CUBE(26),
    PT_PROGRAM(27),
    PT_STRING(28),
    PT_STORAGE_BUFFER(29),
    PT_TYPE(30),
    PT_UNIFORM_BUFFER(31),
    PT_IMAGE2D_STORE_BUFFER(32),
    PT_IMAGE2D_STORE_ARRAY_BUFFER(33),
    PT_IMAGE3D_STORE_BUFFER(34),
    PT_UNIFORM_TEXEL_BUFFER(35),
    PT_STORAGE_TEXEL_BUFFER(36),
    PT_COLOR_LUT(37),
    PT_IMAGE2D_BUFFER(38),
    PT_IMAGE2D_ARRAY_BUFFER(39),
    PT_IMAGE3D_BUFFER(40),
    PT_ACCELERATION_STRUCTURE(41),
    PT_BUFFER_REFERENCE(42),
    PT_MAX(43),
    ;

    private final int code;

    RenderParmType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
