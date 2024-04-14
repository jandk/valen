package be.twofold.valen.reader.decl.renderparm;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.decl.parser.*;
import be.twofold.valen.reader.decl.renderparm.enums.*;
import be.twofold.valen.reader.image.*;
import be.twofold.valen.resource.*;
import jakarta.inject.*;

import java.util.*;

public final class RenderParmReader implements ResourceReader<RenderParm> {
    @Inject
    public RenderParmReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.RsStreamFile
               && entry.nameString().startsWith("generated/decls/renderparm/");
    }

    @Override
    public RenderParm read(BetterBuffer buffer, Resource resource) {
        var bytes = buffer.getBytes(buffer.length());
        var parser = new DeclParser(new String(bytes), true);

        parser.expect(DeclTokenType.OpenBrace);
        var parmType = parseRenderParmType(parser.expectName());
        var value = parseValue(parser, parmType);
        var extras = parseExtras(parser);
        parser.expect(DeclTokenType.CloseBrace);
        return null;
    }

    final class ParmExtras {
        ParmEdit parmEdit;
        ParmScope parmScope;
        boolean cubeFilterTexture;
        boolean streamed;
        boolean globallyIndexed;
        boolean editable;
        boolean envNoInterpolation;
        boolean fftBloom;
    }

    private ParmExtras parseExtras(DeclParser parser) {
        if (parser.peek().type() == DeclTokenType.CloseBrace) {
            return null;
        }

        var result = new ParmExtras();

        while (parser.peek().type() != DeclTokenType.CloseBrace) {
            var edit = parseParmEdit(parser);
            if (edit != null) {
                result.parmEdit = edit;
                continue;
            }

            var scope = parseParmScope(parser);
            if (scope != null) {
                result.parmScope = scope;
                continue;
            }

            var flags = parseFlags(parser, result);
            if (flags) {
                continue;
            }

            var token = parser.expectName();
            if (token.equalsIgnoreCase("materialKind")) {
                parser.expect(DeclTokenType.Assign);
                var kind = parseImageTextureMaterialKind(parser.expectName());
                continue;
            }

            if (token.equalsIgnoreCase("smoothnessNormalParm")) {
                parser.expect(DeclTokenType.Assign);
                var name = parser.expectName();
                continue;
            }
        }


        return result;
    }

    private boolean parseFlags(DeclParser parser, ParmExtras extras) {
        var token = parser.peek();
        if (token.type() == DeclTokenType.CloseBrace) {
            return false;
        }

        var result = switch (token.value()) {
            case "streamed" -> {
                extras.streamed = true;
                extras.globallyIndexed = true;
                yield true;
            }
            case "globallyindexed" -> extras.globallyIndexed = true;
            case "material", "edit" -> extras.editable = true;
            case "env_nointerp" -> extras.envNoInterpolation = true;
            case "fftBloom" -> extras.fftBloom = true;
            default -> false;
        };

        if (result) {
            parser.expectName();
        }
        return result;
    }

    private ParmScope parseParmScope(DeclParser parser) {
        var token = parser.peekName().toLowerCase();
        var parmScope = switch (token) {
            case "view" -> ParmScope.PSCP_VIEW;
            case "instance" -> ParmScope.PSCP_INSTANCE;
            case "surface" -> ParmScope.PSCP_SURFACE;
            default -> null;
        };
        if (parmScope != null) {
            parser.expectName();
        }
        return parmScope;
    }

    private static ParmEdit parseParmEdit(DeclParser parser) {
        String token = parser.peekName().toLowerCase();
        var parmEdit = switch (token) {
            case "bool" -> new ParmEdit.Bool();
            case "srgb", "srgba" -> new ParmEdit.Srgba();
            case "color" -> new ParmEdit.Color();
            case "range" -> {
                parser.expectName();
                Number min = parser.expectNumber();
                parser.expect(DeclTokenType.Comma);
                Number max = parser.expectNumber();
                yield new ParmEdit.Range(min, max);
            }
            default -> null;
        };
        if (parmEdit != null && !(parmEdit instanceof ParmEdit.Range)) {
            parser.expectName();
        }
        return parmEdit;
    }


    private RenderParm parseValue(DeclParser parser, RenderParmType type) {
        return switch (type) {
            case PT_F32_VEC4 -> {
                var values = readVector(parser, 4);
                yield new RenderParm.F32Vec4(new Vector4(values[0], values[1], values[2], values[3]));
            }
            case PT_F32_VEC3 -> {
                var values = readVector(parser, 3);
                yield new RenderParm.F32Vec3(new Vector3(values[0], values[1], values[2]));
            }
            case PT_F32_VEC2 -> {
                var values = readVector(parser, 2);
                yield new RenderParm.F32Vec2(new Vector2(values[0], values[1]));
            }
            case PT_F32 -> new RenderParm.F32(parser.expectNumber().floatValue());
            case PT_UI32, PT_SI32 -> new RenderParm.I32(parser.expectNumber().intValue());
            case PT_BOOL -> new RenderParm.Bool(parser.expectBoolean());

            case PT_TEXTURE_2D, PT_TEXTURE_3D, PT_TEXTURE_CUBE, PT_TEXTURE_ARRAY_2D, PT_TEXTURE_ARRAY_CUBE,
                 PT_TEXTURE_MULTISAMPLE_2D, PT_TEXTURE_STENCIL -> {
                var props = parseImageProperties(parser);
                var name = parser.expectName();
                if (type == RenderParmType.PT_TEXTURE_CUBE) {
                    props.type = ImageTextureType.TT_CUBIC;
                }
                // TODO: Random shit for environment renderparm
                yield new RenderParm.Texture(props, name);
            }

            case PT_SAMPLER, PT_SAMPLER_SHADOW_2D, PT_SAMPLER_SHADOW_3D, PT_SAMPLER_SHADOW_CUBE -> {
                var value = parser.next().value();
                value = value.equals("0") ? null : value;
                yield new RenderParm.Sampler(value);
            }

            case PT_PROGRAM -> {
                var value = parser.next().value();
                value = value.equals("0") ? null : value;
                yield new RenderParm.Program(value);
            }

            case PT_STRING -> {
                DeclToken token = parser.peek();
                String value = switch (token.type()) {
                    case String, Name -> {
                        String result = token.value();
                        parser.next();
                        yield result;
                    }
                    case CloseBrace -> null;
                    default -> throw new UnsupportedOperationException();
                };
                yield new RenderParm.Str(value);
            }

            case PT_STORAGE_BUFFER -> {
                Set<BufferViewFlag> flags = readStorageBufferFlags(parser);
                String name = parser.expectName();
                yield new RenderParm.StorageBuffer(flags, name);
            }

            case PT_TYPE -> new RenderParm.Type(parseType(parser));
            case PT_UNIFORM_BUFFER -> new RenderParm.UniformBuffer(parser.expectNumber().intValue());

            case PT_IMAGE2D_STORE_BUFFER, PT_IMAGE2D_STORE_ARRAY_BUFFER, PT_IMAGE3D_STORE_BUFFER, PT_IMAGE2D_BUFFER,
                 PT_IMAGE2D_ARRAY_BUFFER, PT_IMAGE3D_BUFFER -> {
                var format = parseImageBufferFormat(parser.expectName());
                yield new RenderParm.ImageBuffer(format);
            }

            case PT_UNIFORM_TEXEL_BUFFER, PT_STORAGE_TEXEL_BUFFER -> {
                var format = parseImageBufferFormat(parser.expectName());
                yield new RenderParm.TexelBuffer(format);
            }

            case PT_COLOR_LUT -> new RenderParm.ColorLut(parser.next().value());
            case PT_ACCELERATION_STRUCTURE -> new RenderParm.AccelerationStructure();

            default -> throw new UnsupportedOperationException("Unsupported type: " + type);
        };
    }

    private static final Set<String> Skipped = Set.of(
        "borderclamp",
        "clamp",
        "clamps",
        "clampt",
        "linear",
        "mirror",
        "nearest"
    );

    private ImageProperties parseImageProperties(DeclParser parser) {
        var props = new ImageProperties();
        while (true) {
            String name = parser.peekName().toLowerCase();
            if (Skipped.contains(name)) {
                parser.expectName();
                continue;
            }

            ImageTextureFormat imageTextureFormat = parseImageTextureFormat(name);
            if (imageTextureFormat != null) {
                props.format = imageTextureFormat;
                parser.expectName();
                continue;
            }

            Integer padding = parsePadding(name);
            if (padding != null) {
                props.padding = padding;
                parser.expectName();
                continue;
            }

            switch (name) {
                case "fullscalebias" -> {
                    props.fullScaleBias = true;
                    parser.expectName();
                    continue;
                }
                case "fftbloom" -> {
                    props.fftBloom = true;
                    parser.expectName();
                    continue;
                }
                case "nomips" -> {
                    props.noMips = true;
                    parser.expectName();
                    continue;
                }
            }

            break;
        }
        return props;
    }

    private Integer parsePadding(String token) {
        return switch (token) {
            case "pad2" -> 2;
            case "pad4" -> 4;
            case "pad8" -> 8;
            case "pad16" -> 16;
            default -> null;
        };
    }

    private String parseType(DeclParser parser) {
        parser.expect(DeclTokenType.OpenBrace);

        var builder = new StringBuilder("struct\n{\n\t");
        while (true) {
            var token = parser.next();
            if (token.type() == DeclTokenType.CloseBrace) {
                break;
            }
            if (token.type() == DeclTokenType.Semicolon) {
                builder.setLength(builder.length() - 1);
                builder.append(";\n\t");
                continue;
            }
            builder.append(token.value()).append(" ");
        }
        builder.setLength(builder.length() - 1);
        builder.append("}");
        return builder.toString();
    }

    private static Set<BufferViewFlag> readStorageBufferFlags(DeclParser parser) {
        var flags = EnumSet.noneOf(BufferViewFlag.class);
        while (true) {
            var name = parser.peekName().toLowerCase();
            switch (name) {
                case "writable" -> flags.add(BufferViewFlag.BVF_WRITABLE);
                case "coherent" -> flags.add(BufferViewFlag.BVF_COHERENT);
                case "instancedescriptorsetlayout", "surfacedescriptorsetlayout" -> {
                    // Just ignore these
                }
                default -> {
                    return flags;
                }
            }
            parser.expectName();
        }
    }

    public static boolean compareCI(String a, String b) {
        return a.equalsIgnoreCase(b);
    }

    private static ImageTextureFormat parseImageTextureFormat(String token) {
        return switch (token) {
            case "alpha" -> ImageTextureFormat.FMT_ALPHA;
            case "bc4" -> ImageTextureFormat.FMT_BC4;
            case "bc5" -> ImageTextureFormat.FMT_BC5;
            case "bc6h", "bc6huf16" -> ImageTextureFormat.FMT_BC6H_UF16;
            case "bc6hsf16" -> ImageTextureFormat.FMT_BC6H_SF16;
            case "bc7" -> ImageTextureFormat.FMT_BC7;
            case "float" -> ImageTextureFormat.FMT_RGBA16F;
            case "hqcompress", "hqcompressnormal" -> ImageTextureFormat.FMT_BC3;
            case "r8" -> ImageTextureFormat.FMT_R8;
            case "rg16f" -> ImageTextureFormat.FMT_RG16F;
            case "rg32f" -> ImageTextureFormat.FMT_RG32F;
            case "rg8" -> ImageTextureFormat.FMT_RG8;
            case "uncompressed" -> ImageTextureFormat.FMT_RGBA8;
            default -> null;
        };
    }

    private float[] readVector(DeclParser parser, int n) {
        var values = new float[n];
        if (!parser.match(DeclTokenType.OpenBrace)) {
            var number = parser.expectNumber().floatValue();
            Arrays.fill(values, number);
            return values;
        }

        values[0] = parser.expectNumber().floatValue();
        while (!parser.match(DeclTokenType.CloseBrace)) {
            for (var i = 1; i < n; i++) {
                if (parser.peek().type() == DeclTokenType.CloseBrace) {
                    break;
                }
                parser.expect(DeclTokenType.Comma);
                values[i] = parser.expectNumber().floatValue();
            }
        }
        return values;
    }

    private RenderParmType parseRenderParmType(String name) {
        return switch (name.toLowerCase()) {
            case "tex", "tex2d" -> RenderParmType.PT_TEXTURE_2D;
            case "tex3d" -> RenderParmType.PT_TEXTURE_3D;
            case "texcube", "environment" -> RenderParmType.PT_TEXTURE_CUBE;
            case "texarray2d" -> RenderParmType.PT_TEXTURE_ARRAY_2D;
            case "texarraycube" -> RenderParmType.PT_TEXTURE_ARRAY_CUBE;
            case "texmultisample2d" -> RenderParmType.PT_TEXTURE_MULTISAMPLE_2D;
            case "texstencil" -> RenderParmType.PT_TEXTURE_STENCIL;
            case "sampler" -> RenderParmType.PT_SAMPLER;
            case "samplershadow2d" -> RenderParmType.PT_SAMPLER_SHADOW_2D;
            case "samplershadow3d" -> RenderParmType.PT_SAMPLER_SHADOW_3D;
            case "samplershadowcube" -> RenderParmType.PT_SAMPLER_SHADOW_CUBE;
            case "program" -> RenderParmType.PT_PROGRAM;
            case "f32vec4", "vec" -> RenderParmType.PT_F32_VEC4;
            case "f32vec3" -> RenderParmType.PT_F32_VEC3;
            case "f32vec2" -> RenderParmType.PT_F32_VEC2;
            case "f32", "scalar" -> RenderParmType.PT_F32;
            case "ui32" -> RenderParmType.PT_UI32;
            case "si32" -> RenderParmType.PT_SI32;
            case "bool" -> RenderParmType.PT_BOOL;
            case "string" -> RenderParmType.PT_STRING;
            case "structuredbuffer" -> RenderParmType.PT_STORAGE_BUFFER;
            case "uniformbuffer" -> RenderParmType.PT_UNIFORM_BUFFER;
            case "imagebuffer2d" -> RenderParmType.PT_IMAGE2D_BUFFER;
            case "imagebuffer2darray" -> RenderParmType.PT_IMAGE2D_ARRAY_BUFFER;
            case "imagebuffer3d" -> RenderParmType.PT_IMAGE3D_BUFFER;
            case "imagestorebuffer2d" -> RenderParmType.PT_IMAGE2D_STORE_BUFFER;
            case "imagestorebuffer2darray" -> RenderParmType.PT_IMAGE2D_STORE_ARRAY_BUFFER;
            case "imagestorebuffer3d" -> RenderParmType.PT_IMAGE3D_STORE_BUFFER;
            case "uniformtexelbuffer" -> RenderParmType.PT_UNIFORM_TEXEL_BUFFER;
            case "storagetexelbuffer" -> RenderParmType.PT_STORAGE_TEXEL_BUFFER;
            case "accelerationstructure" -> RenderParmType.PT_ACCELERATION_STRUCTURE;
            case "struct" -> RenderParmType.PT_TYPE;
            case "colorlut" -> RenderParmType.PT_COLOR_LUT;
            default -> throw new IllegalArgumentException("Unknown render parm type: " + name);
        };
    }

    private ImageBufferFormat parseImageBufferFormat(String name) {
        return switch (name.toLowerCase()) {
            case "float" -> ImageBufferFormat.IBF_FLOAT;
            case "int" -> ImageBufferFormat.IBF_INT;
            case "uint" -> ImageBufferFormat.IBF_UINT;
            case "rgba32f" -> ImageBufferFormat.IBF_RGBA32F;
            case "rgba16f" -> ImageBufferFormat.IBF_RGBA16F;
            case "rg32f" -> ImageBufferFormat.IBF_RG32F;
            case "rg16f" -> ImageBufferFormat.IBF_RG16F;
            case "r11f_g11f_b10f" -> ImageBufferFormat.IBF_R11F_G11F_B10F;
            case "r32f" -> ImageBufferFormat.IBF_R32F;
            case "r16f" -> ImageBufferFormat.IBF_R16F;
            case "rgba16" -> ImageBufferFormat.IBF_RGBA16;
            case "rgb10_a2" -> ImageBufferFormat.IBF_RGB10_A2;
            case "rgba8" -> ImageBufferFormat.IBF_RGBA8;
            case "rg16" -> ImageBufferFormat.IBF_RG16;
            case "rg8" -> ImageBufferFormat.IBF_RG8;
            case "r16" -> ImageBufferFormat.IBF_R16;
            case "r8" -> ImageBufferFormat.IBF_R8;
            case "rgba16_snorm" -> ImageBufferFormat.IBF_RGBA16_SNORM;
            case "rgba8_snorm" -> ImageBufferFormat.IBF_RGBA8_SNORM;
            case "rg16_snorm" -> ImageBufferFormat.IBF_RG16_SNORM;
            case "rg8_snorm" -> ImageBufferFormat.IBF_RG8_SNORM;
            case "r16_snorm" -> ImageBufferFormat.IBF_R16_SNORM;
            case "r8_snorm" -> ImageBufferFormat.IBF_R8_SNORM;
            case "rgba32i" -> ImageBufferFormat.IBF_RGBA32I;
            case "rgba16i" -> ImageBufferFormat.IBF_RGBA16I;
            case "rgba8i" -> ImageBufferFormat.IBF_RGBA8I;
            case "rg32i" -> ImageBufferFormat.IBF_RG32I;
            case "rg16i" -> ImageBufferFormat.IBF_RG16I;
            case "rg8i" -> ImageBufferFormat.IBF_RG8I;
            case "r32i" -> ImageBufferFormat.IBF_R32I;
            case "r16i" -> ImageBufferFormat.IBF_R16I;
            case "r8i" -> ImageBufferFormat.IBF_R8I;
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
            default -> throw new IllegalArgumentException("Unknown image buffer format: " + name);
        };
    }

    private static ImageTextureMaterialKind parseImageTextureMaterialKind(String name) {
        return switch (name) {
            case "albedo" -> ImageTextureMaterialKind.TMK_ALBEDO;
            case "specular" -> ImageTextureMaterialKind.TMK_SPECULAR;
            case "normal" -> ImageTextureMaterialKind.TMK_NORMAL;
            case "smoothness" -> ImageTextureMaterialKind.TMK_SMOOTHNESS;
            case "cover" -> ImageTextureMaterialKind.TMK_COVER;
            case "colormask" -> ImageTextureMaterialKind.TMK_COLORMASK;
            case "bloommask" -> ImageTextureMaterialKind.TMK_BLOOMMASK;
            case "sssmask" -> ImageTextureMaterialKind.TMK_SSSMASK;
            case "heightmap" -> ImageTextureMaterialKind.TMK_HEIGHTMAP;
            case "decalalbedo" -> ImageTextureMaterialKind.TMK_DECALALBEDO;
            case "decalnormal" -> ImageTextureMaterialKind.TMK_DECALNORMAL;
            case "decalspecular" -> ImageTextureMaterialKind.TMK_DECALSPECULAR;
            case "lightproject" -> ImageTextureMaterialKind.TMK_LIGHTPROJECT;
            case "particle" -> ImageTextureMaterialKind.TMK_PARTICLE;
            case "lightmap" -> ImageTextureMaterialKind.TMK_LIGHTMAP;
            case "lightmapdir" -> ImageTextureMaterialKind.TMK_LIGHTMAP_DIRECTIONAL;
            case "blendmask" -> ImageTextureMaterialKind.TMK_BLENDMASK;
            default -> ImageTextureMaterialKind.TMK_NONE;
        };
    }
}
