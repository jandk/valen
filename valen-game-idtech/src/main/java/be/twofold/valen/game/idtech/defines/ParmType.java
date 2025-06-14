package be.twofold.valen.game.idtech.defines;

public enum ParmType {
    PT_F32_VEC4,
    PT_F32_VEC3,
    PT_F32_VEC2,
    PT_F32,
    PT_UI32,
    PT_SI32,
    PT_BOOL,
    PT_TEXTURE_2D,
    PT_TEXTURE_3D,
    PT_TEXTURE_CUBE,
    PT_TEXTURE_ARRAY_2D,
    PT_TEXTURE_ARRAY_CUBE,
    PT_TEXTURE_MULTISAMPLE_2D,
    PT_TEXTURE_STENCIL,
    PT_TEXTURE_SHADOW_2D,
    PT_TEXTURE_SHADOW_3D,
    PT_TEXTURE_SHADOW_CUBE,
    PT_SAMPLER,
    PT_SAMPLER_SHADOW_2D,
    PT_SAMPLER_SHADOW_3D,
    PT_SAMPLER_SHADOW_CUBE,
    PT_PROGRAM,
    PT_STRING,
    PT_STORAGE_BUFFER,
    PT_TYPE,
    PT_UNIFORM_BUFFER,
    PT_IMAGE2D_STORE_BUFFER,
    PT_IMAGE2D_STORE_ARRAY_BUFFER,
    PT_IMAGE3D_STORE_BUFFER,
    PT_UNIFORM_TEXEL_BUFFER,
    PT_STORAGE_TEXEL_BUFFER,
    PT_COLOR_LUT,
    PT_IMAGE2D_BUFFER,
    PT_IMAGE2D_ARRAY_BUFFER,
    PT_IMAGE3D_BUFFER,
    PT_ACCELERATION_STRUCTURE,

    // Great Circle
    PT_F16_VEC4,
    PT_F16_VEC3,
    PT_F16_VEC2,
    PT_F16,
    PT_TEXTURE_2D_HALF,
    PT_TEXTURE_2D_UI,
    PT_BUFFER_REFERENCE,

    // Dark Ages
    PT_IMAGECUBE_STORE_BUFFER,
    PT_IMAGECUBE_BUFFER,
    ;

    public static ParmType parse(String parmType) {
        return switch (parmType.toLowerCase()) {
            case "tex", "tex2d" -> ParmType.PT_TEXTURE_2D;
            case "tex2dhalf" -> ParmType.PT_TEXTURE_2D_HALF;
            case "tex2du" -> ParmType.PT_TEXTURE_2D_UI;
            case "tex3d" -> ParmType.PT_TEXTURE_3D;
            case "texcube", "environment" -> ParmType.PT_TEXTURE_CUBE;
            case "texarray2d" -> ParmType.PT_TEXTURE_ARRAY_2D;
            case "texarraycube" -> ParmType.PT_TEXTURE_ARRAY_CUBE;
            case "texmultisample2d" -> ParmType.PT_TEXTURE_MULTISAMPLE_2D;
            case "texstencil" -> ParmType.PT_TEXTURE_STENCIL;
            case "sampler" -> ParmType.PT_SAMPLER;
            case "samplershadow2d" -> ParmType.PT_SAMPLER_SHADOW_2D;
            case "samplershadow3d" -> ParmType.PT_SAMPLER_SHADOW_3D;
            case "samplershadowcube" -> ParmType.PT_SAMPLER_SHADOW_CUBE;
            case "program" -> ParmType.PT_PROGRAM;
            case "f32vec4", "vec" -> ParmType.PT_F32_VEC4;
            case "f32vec3" -> ParmType.PT_F32_VEC3;
            case "f32vec2" -> ParmType.PT_F32_VEC2;
            case "f32", "scalar" -> ParmType.PT_F32;
            case "f16vec4" -> ParmType.PT_F16_VEC4;
            case "f16vec3" -> ParmType.PT_F16_VEC3;
            case "f16vec2" -> ParmType.PT_F16_VEC2;
            case "f16" -> ParmType.PT_F16;
            case "ui32" -> ParmType.PT_UI32;
            case "si32" -> ParmType.PT_SI32;
            case "bool" -> ParmType.PT_BOOL;
            case "string" -> ParmType.PT_STRING;
            case "structuredbuffer" -> ParmType.PT_STORAGE_BUFFER;
            case "uniformbuffer" -> ParmType.PT_UNIFORM_BUFFER;
            case "imagebuffer2d" -> ParmType.PT_IMAGE2D_BUFFER;
            case "imagebuffer2darray" -> ParmType.PT_IMAGE2D_ARRAY_BUFFER;
            case "imagebuffer3d" -> ParmType.PT_IMAGE3D_BUFFER;
            case "imagestorebuffer2d" -> ParmType.PT_IMAGE2D_STORE_BUFFER;
            case "imagestorebuffer2darray" -> ParmType.PT_IMAGE2D_STORE_ARRAY_BUFFER;
            case "imagestorebuffer3d" -> ParmType.PT_IMAGE3D_STORE_BUFFER;
            case "imagestorebuffercube" -> ParmType.PT_IMAGECUBE_STORE_BUFFER;
            case "uniformtexelbuffer" -> ParmType.PT_UNIFORM_TEXEL_BUFFER;
            case "storagetexelbuffer" -> ParmType.PT_STORAGE_TEXEL_BUFFER;
            case "accelerationstructure" -> ParmType.PT_ACCELERATION_STRUCTURE;
            case "struct" -> ParmType.PT_TYPE;
            case "structuredbufferreference" -> ParmType.PT_BUFFER_REFERENCE;
            case "colorlut" -> ParmType.PT_COLOR_LUT;
            default -> throw new IllegalArgumentException("Unknown render parm type: " + parmType);
        };
    }
}
