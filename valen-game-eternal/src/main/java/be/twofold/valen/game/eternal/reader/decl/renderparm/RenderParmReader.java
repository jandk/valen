package be.twofold.valen.game.eternal.reader.decl.renderparm;

import be.twofold.valen.core.game.AssetReader;
import be.twofold.valen.core.io.DataSource;
import be.twofold.valen.core.math.Vector2;
import be.twofold.valen.core.math.Vector3;
import be.twofold.valen.core.math.Vector4;
import be.twofold.valen.game.eternal.EternalAsset;
import be.twofold.valen.game.eternal.defines.*;
import be.twofold.valen.game.eternal.reader.decl.renderparm.enums.ParmEdit;
import be.twofold.valen.game.eternal.resource.ResourceType;
import be.twofold.valen.game.idtech.decl.parser.DeclParser;
import be.twofold.valen.game.idtech.decl.parser.DeclToken;
import be.twofold.valen.game.idtech.decl.parser.DeclTokenType;

import java.io.IOException;
import java.util.*;

public final class RenderParmReader implements AssetReader<RenderParm, EternalAsset> {
    public static final Map<ParmType, Set<String>> ParmsByType = new EnumMap<>(ParmType.class);

    public RenderParmReader() {
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.RsStreamFile
            && resource.id().name().name().startsWith("generated/decls/renderparm/");
    }

    @Override
    public RenderParm read(DataSource source, EternalAsset resource) throws IOException {
        var bytes = source.readBytes(Math.toIntExact(source.size()));
        var parser = new DeclParser(new String(bytes), true);

        var result = new RenderParm();
        parser.expect(DeclTokenType.OpenBrace);
        result.parmType = parseParmType(parser.expectName());
        ParmsByType
            .computeIfAbsent(result.parmType, k -> new HashSet<>())
            .add(resource.id().fileName().replace(".decl", ""));

        result.declaredValue = parseValue(parser, result);
        parseExtras(parser, result);
        parser.expect(DeclTokenType.CloseBrace);
        return result;
    }

    private void parseExtras(DeclParser parser, RenderParm result) {
        if (parser.peek().type() == DeclTokenType.CloseBrace) {
            return;
        }

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
                result.materialKind = parseImageTextureMaterialKind(parser.expectName());
                continue;
            }

            if (token.equalsIgnoreCase("smoothnessNormalParm")) {
                parser.expect(DeclTokenType.Assign);
                result.smoothnessNormalParm = parseImageTextureMaterialKind(parser.expectName());
                continue;
            }
        }
    }

    private boolean parseFlags(DeclParser parser, RenderParm result) {
        var token = parser.peek();
        if (token.type() == DeclTokenType.CloseBrace) {
            return false;
        }

        var read = switch (token.value()) {
            case "streamed" -> {
                result.streamed = true;
                result.globallyIndexed = true;
                yield true;
            }
            case "globallyindexed" -> result.globallyIndexed = true;
            case "material", "edit" -> result.editable = true;
            case "env_nointerp" -> result.envNoInterpolation = true;
            case "fftBloom" -> result.fftBloom = true;
            default -> false;
        };

        if (read) {
            parser.expectName();
        }
        return read;
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


    private Object parseValue(DeclParser parser, RenderParm renderParm) {
        switch (renderParm.parmType) {
            case PT_F32_VEC4:
                return readVector4(parser);
            case PT_F32_VEC3:
                return readVector3(parser);
            case PT_F32_VEC2:
                return readVector2(parser);
            case PT_F32:
                return parser.expectNumber().floatValue();
            case PT_UI32:
            case PT_SI32:
                return parser.expectNumber().intValue();
            case PT_BOOL:
                return parser.expectBoolean();
            case PT_TEXTURE_2D:
            case PT_TEXTURE_3D:
            case PT_TEXTURE_CUBE:
            case PT_TEXTURE_ARRAY_2D:
            case PT_TEXTURE_ARRAY_CUBE:
            case PT_TEXTURE_MULTISAMPLE_2D:
            case PT_TEXTURE_STENCIL:
                var props = parseImageProperties(parser);
                props.name = parser.expectName();
                return props;
            case PT_SAMPLER:
            case PT_SAMPLER_SHADOW_2D:
            case PT_SAMPLER_SHADOW_3D:
            case PT_SAMPLER_SHADOW_CUBE:
            case PT_PROGRAM: {
                var value = parser.next().value();
                return value.equals("0") ? null : value;
            }
            case PT_STRING:
                DeclToken token = parser.peek();
                return switch (token.type()) {
                    case String, Name -> {
                        String result = token.value();
                        parser.next();
                        yield result;
                    }
                    case CloseBrace -> null;
                    default -> throw new UnsupportedOperationException();
                };
            case PT_STORAGE_BUFFER:
                Set<BufferViewFlag> flags = readStorageBufferFlags(parser);
                String name = parser.expectName();
                return new RenderParmStorageBuffer(flags, name);
            case PT_TYPE:
                return parseType(parser);
            case PT_UNIFORM_BUFFER:
                return parser.expectNumber().intValue();
            case PT_IMAGE2D_STORE_BUFFER:
            case PT_IMAGE2D_STORE_ARRAY_BUFFER:
            case PT_IMAGE3D_STORE_BUFFER:
            case PT_IMAGE2D_BUFFER:
            case PT_IMAGE2D_ARRAY_BUFFER:
            case PT_IMAGE3D_BUFFER:
            case PT_UNIFORM_TEXEL_BUFFER:
            case PT_STORAGE_TEXEL_BUFFER:
                return ImageBufferFormat.parse(parser.expectName());
            case PT_COLOR_LUT:
                return parser.next().value();
            case PT_ACCELERATION_STRUCTURE:
                return null;
            default:
                throw new UnsupportedOperationException("Unsupported type: " + renderParm);
        }
    }

    private Vector2 readVector2(DeclParser parser) {
        var floats = readVector(parser, 2);
        return new Vector2(floats[0], floats[1]);
    }

    private Vector3 readVector3(DeclParser parser) {
        var floats = readVector(parser, 3);
        return new Vector3(floats[0], floats[1], floats[2]);
    }

    private Vector4 readVector4(DeclParser parser) {
        var floats = readVector(parser, 4);
        return new Vector4(floats[0], floats[1], floats[2], floats[3]);
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
        var result = new ImageProperties();
        while (true) {
            String name = parser.peekName().toLowerCase();
            if (Skipped.contains(name)) {
                parser.expectName();
                continue;
            }


            TextureFormat textureFormat = parseImageTextureFormat(name);
            if (textureFormat != null) {
                result.format = textureFormat;
                parser.expectName();
                continue;
            }

            Integer padding = parsePadding(name);
            if (padding != null) {
                result.padding = padding;
                parser.expectName();
                continue;
            }

            switch (name) {
                case "fullscalebias" -> {
                    result.fullScaleBias = true;
                    parser.expectName();
                    continue;
                }
                case "fftbloom" -> {
                    result.fftBloom = true;
                    parser.expectName();
                    continue;
                }
                case "nomips" -> {
                    result.noMips = true;
                    parser.expectName();
                    continue;
                }
            }

            break;
        }
        return result;
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


    // region Helpers

    private static TextureFormat parseImageTextureFormat(String name) {
        return switch (name.toLowerCase()) {
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

    private TextureMaterialKind parseImageTextureMaterialKind(String name) {
        return switch (name.toLowerCase()) {
            case "albedo" -> TextureMaterialKind.TMK_ALBEDO;
            case "specular" -> TextureMaterialKind.TMK_SPECULAR;
            case "normal" -> TextureMaterialKind.TMK_NORMAL;
            case "smoothness" -> TextureMaterialKind.TMK_SMOOTHNESS;
            case "cover" -> TextureMaterialKind.TMK_COVER;
            case "colormask" -> TextureMaterialKind.TMK_COLORMASK;
            case "bloommask" -> TextureMaterialKind.TMK_BLOOMMASK;
            case "sssmask" -> TextureMaterialKind.TMK_SSSMASK;
            case "heightmap" -> TextureMaterialKind.TMK_HEIGHTMAP;
            case "decalalbedo" -> TextureMaterialKind.TMK_DECALALBEDO;
            case "decalnormal" -> TextureMaterialKind.TMK_DECALNORMAL;
            case "decalspecular" -> TextureMaterialKind.TMK_DECALSPECULAR;
            case "lightproject" -> TextureMaterialKind.TMK_LIGHTPROJECT;
            case "particle" -> TextureMaterialKind.TMK_PARTICLE;
            case "lightmap" -> TextureMaterialKind.TMK_LIGHTMAP;
            case "lightmapdir" -> TextureMaterialKind.TMK_LIGHTMAP_DIRECTIONAL;
            case "blendmask" -> TextureMaterialKind.TMK_BLENDMASK;
            default -> TextureMaterialKind.TMK_NONE;
        };
    }

    private ParmType parseParmType(String name) {
        return switch (name.toLowerCase()) {
            case "tex", "tex2d" -> ParmType.PT_TEXTURE_2D;
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
            case "uniformtexelbuffer" -> ParmType.PT_UNIFORM_TEXEL_BUFFER;
            case "storagetexelbuffer" -> ParmType.PT_STORAGE_TEXEL_BUFFER;
            case "accelerationstructure" -> ParmType.PT_ACCELERATION_STRUCTURE;
            case "struct" -> ParmType.PT_TYPE;
            case "colorlut" -> ParmType.PT_COLOR_LUT;
            default -> throw new IllegalArgumentException("Unknown render parm type: " + name);
        };
    }

    // endregion

}
