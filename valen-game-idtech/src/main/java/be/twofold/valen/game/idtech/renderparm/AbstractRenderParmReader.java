package be.twofold.valen.game.idtech.renderparm;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.idtech.decl.parser.*;
import be.twofold.valen.game.idtech.defines.*;

import java.io.*;
import java.util.*;

public abstract class AbstractRenderParmReader<V extends Asset> implements AssetReader<RenderParm, V> {
    private static final Set<String> Skipped = Set.of(
        "borderclamp",
        "clamp",
        "clamps",
        "clampt",
        "linear",
        "mirror",
        "nearest"
    );

    protected Object parseValue(DeclParser parser, RenderParm renderParm) {
        switch (renderParm.parmType) {
            case PT_F32_VEC4, PT_F16_VEC4:
                return readVector4(parser);
            case PT_F32_VEC3, PT_F16_VEC3:
                return readVector3(parser);
            case PT_F32_VEC2, PT_F16_VEC2:
                return readVector2(parser);
            case PT_F32, PT_F16:
                return parser.expectNumber().floatValue();
            case PT_UI32:
            case PT_SI32:
                return parser.expectNumber().intValue();
            case PT_BOOL:
                if (parser.peek().type() == DeclTokenType.Name) {
                    return parser.expectBoolean();
                } else {
                    return parser.expectNumber().intValue() != 0;
                }
            case PT_TEXTURE_2D:
            case PT_TEXTURE_2D_HALF:
            case PT_TEXTURE_2D_UI:
            case PT_TEXTURE_3D:
            case PT_TEXTURE_CUBE:
            case PT_TEXTURE_ARRAY_2D:
            case PT_TEXTURE_ARRAY_CUBE:
            case PT_TEXTURE_MULTISAMPLE_2D:
            case PT_TEXTURE_STENCIL:
                var props = parseImageProperties(parser);
                props.name = parser.expectNameOrString();
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
            case PT_BUFFER_REFERENCE:
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
            case PT_IMAGECUBE_STORE_BUFFER:
            case PT_IMAGE2D_BUFFER:
            case PT_IMAGE2D_ARRAY_BUFFER:
            case PT_IMAGE3D_BUFFER:
            case PT_IMAGECUBE_BUFFER:
            case PT_UNIFORM_TEXEL_BUFFER:
            case PT_STORAGE_TEXEL_BUFFER:
                return ImageBufferFormat.parse(parser.expectName());
            case PT_COLOR_LUT:
                return parser.next().value();
            case PT_ACCELERATION_STRUCTURE:
                return null;
            default:
                throw new UnsupportedOperationException("Unsupported type: " + renderParm.parmType);
        }
    }

    protected Set<BufferViewFlag> readStorageBufferFlags(DeclParser parser) {
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

    protected float[] readVector(DeclParser parser, int n) {
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

    protected String parseType(DeclParser parser) {
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

    protected Integer parsePadding(String token) {
        return switch (token) {
            case "pad2" -> 2;
            case "pad4" -> 4;
            case "pad8" -> 8;
            case "pad16" -> 16;
            default -> null;
        };
    }

    protected ImageProperties parseImageProperties(DeclParser parser) {
        var result = new ImageProperties();
        while (true) {
            DeclToken peek = parser.peek();
            if (peek.type() != DeclTokenType.Name) {
                return result;
            }
            String name = peek.value();
            if (Skipped.contains(name)) {
                parser.expectName();
                continue;
            }


            TextureFormat textureFormat = TextureFormat.parse(name);
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
                case "streamed" -> {
                    result.streamed = true;
                    parser.expectName();
                    continue;
                }
            }

            break;
        }
        return result;
    }

    protected ParmScope parseParmScope(DeclParser parser) {
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

    protected ParmEdit parseParmEdit(DeclParser parser) {
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

    protected boolean parseFlags(DeclParser parser, RenderParm result) {
        var token = parser.peek();
        if (token.type() == DeclTokenType.CloseBrace) {
            return false;
        }

        var read = switch (token.value().toLowerCase()) {
            case "streamed" -> result.streamed = true;
            case "globallyindexed" -> result.globallyIndexed = true;
            case "material", "edit" -> result.editable = true;
            case "env_nointerp" -> result.envNoInterpolation = true;
            case "fftbloom" -> result.fftBloom = true;
            case "sfsfeedback" -> result.sfsFeedback = true;
            case "materialfeedback" -> result.materialFeedback = true;
            case "divergent" -> result.divergent = true;
            default -> false;
        };

        if (read) {
            parser.expectName();
        }
        return read;
    }

    protected void parseExtras(DeclParser parser, RenderParm result) {
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

            var token = parser.expectName().toLowerCase();
            switch (token) {
                case "materialkind" -> {
                    parser.expect(DeclTokenType.Assign);
                    result.materialKind = TextureMaterialKind.parse(parser.expectName());
                    continue;
                }
                case "smoothnessnormalparm" -> {
                    parser.expect(DeclTokenType.Assign);
                    result.smoothnessNormalParm = TextureMaterialKind.parse(parser.expectName());
                    continue;
                }
                case "autobind" -> {
                    // Just ignore this one
                    if (parser.match(DeclTokenType.OpenBrace)) {
                        parser.expectName();
                        parser.expect(DeclTokenType.CloseBrace);
                    }
                    continue;
                }
                case "cvar" -> {
                    // Just ignore this one
                    continue;
                }
            }

            throw new IllegalArgumentException("Unsupported parm type: " + token);
        }
    }

    protected RenderParm read(DataSource source) throws IOException {
        var bytes = source.readBytes(Math.toIntExact(source.size()));
        var parser = new DeclParser(new String(bytes), true);

        var result = new RenderParm();
        parser.expect(DeclTokenType.OpenBrace);
        result.parmType = ParmType.parse(parser.expectName());

        result.declaredValue = parseValue(parser, result);
        if (result.declaredValue instanceof ImageProperties imageProperties) {
            result.streamed = imageProperties.streamed;
        }

        parseExtras(parser, result);
        parser.expect(DeclTokenType.CloseBrace);
        return result;
    }
}
