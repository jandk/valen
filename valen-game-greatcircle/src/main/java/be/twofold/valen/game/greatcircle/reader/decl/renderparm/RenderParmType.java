package be.twofold.valen.game.greatcircle.reader.decl.renderparm;

public enum RenderParmType {
    PT_F32_VEC4(0),
    PT_F32_VEC3(1),
    PT_F32_VEC2(2),
    PT_F32(3),
    PT_UI32(4),
    PT_SI32(5),
    PT_BOOL(6),
    PT_TEXTURE_2D(7),
    PT_TEXTURE_3D(8),
    PT_TEXTURE_CUBE(9),
    PT_TEXTURE_ARRAY_2D(10),
    PT_TEXTURE_ARRAY_CUBE(11),
    PT_TEXTURE_MULTISAMPLE_2D(12),
    PT_TEXTURE_STENCIL(13),
    PT_TEXTURE_SHADOW_2D(14),
    PT_TEXTURE_SHADOW_3D(15),
    PT_TEXTURE_SHADOW_CUBE(16),
    PT_SAMPLER(17),
    PT_SAMPLER_SHADOW_2D(18),
    PT_SAMPLER_SHADOW_3D(19),
    PT_SAMPLER_SHADOW_CUBE(20),
    PT_PROGRAM(21),
    PT_STRING(22),
    PT_STORAGE_BUFFER(23),
    PT_TYPE(24),
    PT_UNIFORM_BUFFER(25),
    PT_IMAGE2D_STORE_BUFFER(26),
    PT_IMAGE2D_STORE_ARRAY_BUFFER(27),
    PT_IMAGE3D_STORE_BUFFER(28),
    PT_UNIFORM_TEXEL_BUFFER(29),
    PT_STORAGE_TEXEL_BUFFER(30),
    PT_COLOR_LUT(31),
    PT_IMAGE2D_BUFFER(32),
    PT_IMAGE2D_ARRAY_BUFFER(33),
    PT_IMAGE3D_BUFFER(34),
    PT_ACCELERATION_STRUCTURE(35);

    private final int code;

    RenderParmType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
