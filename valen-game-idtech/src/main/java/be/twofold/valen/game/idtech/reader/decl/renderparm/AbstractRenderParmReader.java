package be.twofold.valen.game.idtech.reader.decl.renderparm;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.idtech.lexer.*;
import be.twofold.valen.game.idtech.reader.image.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.util.*;

public abstract class AbstractRenderParmReader<V extends Asset> implements AssetReader.Binary<RenderParm, V> {
    private static final Set<String> Skipped = Set.of(
        "borderclamp",
        "clamp",
        "clamps",
        "clampt",
        "linear",
        "mirror",
        "nearest"
    );

    protected Object parseValue(IdLexer lexer, RenderParm renderParm) {
        switch (renderParm.parmType) {
            case PT_F32_VEC4, PT_F16_VEC4:
                return readVector4(lexer);
            case PT_F32_VEC3, PT_F16_VEC3:
                return readVector3(lexer);
            case PT_F32_VEC2, PT_F16_VEC2:
                return readVector2(lexer);
            case PT_F32, PT_F16:
                return lexer.parseFloat();
            case PT_UI32:
            case PT_SI32:
                return readInt(lexer);
            case PT_BOOL:
                return readBool(lexer);
            case PT_TEXTURE_2D:
            case PT_TEXTURE_2D_HALF:
            case PT_TEXTURE_2D_UI:
            case PT_TEXTURE_3D:
            case PT_TEXTURE_CUBE:
            case PT_TEXTURE_ARRAY_2D:
            case PT_TEXTURE_ARRAY_CUBE:
            case PT_TEXTURE_MULTISAMPLE_2D:
            case PT_TEXTURE_STENCIL:
                var props = parseImageProperties(lexer);
                props.name = expectNameOrString(lexer);
                return props;
            case PT_SAMPLER:
            case PT_SAMPLER_SHADOW_2D:
            case PT_SAMPLER_SHADOW_3D:
            case PT_SAMPLER_SHADOW_CUBE:
            case PT_PROGRAM: {
                var value = lexer.expectAnyToken().value();
                return value.equals("0") ? null : value;
            }
            case PT_STRING:
                IdToken token = lexer.expectAnyToken();
                return switch (token.type()) {
                    case TT_STRING, TT_NAME -> token.value();
                    case TT_PUNCTUATION -> {
                        if (!token.isPunctuation(LexerPunctuation.P_BRACECLOSE)) {
                            throw new UnsupportedOperationException();
                        }
                        lexer.unreadToken();
                        yield null;
                    }
                    default -> throw new UnsupportedOperationException();
                };
            case PT_STORAGE_BUFFER:
            case PT_BUFFER_REFERENCE:
                Set<BufferViewFlag> flags = readStorageBufferFlags(lexer);
                String name = lexer.expectName().value();
                return new RenderParmStorageBuffer(flags, name);
            case PT_TYPE:
                return parseType(lexer);
            case PT_UNIFORM_BUFFER:
                return readInt(lexer);
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
                return ImageBufferFormat.parse(lexer.expectName().value());
            case PT_COLOR_LUT:
                return lexer.expectAnyToken().value();
            case PT_ACCELERATION_STRUCTURE:
                return null;
            default:
                throw new UnsupportedOperationException("Unsupported type: " + renderParm.parmType);
        }
    }

    protected Set<BufferViewFlag> readStorageBufferFlags(IdLexer lexer) {
        var flags = EnumSet.noneOf(BufferViewFlag.class);
        while (true) {
            var name = peekValue(lexer).toLowerCase();
            switch (name) {
                case "writable" -> flags.add(BufferViewFlag.BVF_WRITABLE);
                case "coherent" -> flags.add(BufferViewFlag.BVF_COHERENT);
                case "instancedescriptorsetlayout", "surfacedescriptorsetlayout", "skipoobchecks" -> {
                    // ignore
                }
                default -> {
                    return flags;
                }
            }
            lexer.expectName();
        }
    }

    private Vector2 readVector2(IdLexer lexer) {
        var floats = readVector(lexer, 2);
        return new Vector2(floats[0], floats[1]);
    }

    private Vector3 readVector3(IdLexer lexer) {
        var floats = readVector(lexer, 3);
        return new Vector3(floats[0], floats[1], floats[2]);
    }

    private Vector4 readVector4(IdLexer lexer) {
        var floats = readVector(lexer, 4);
        return new Vector4(floats[0], floats[1], floats[2], floats[3]);
    }

    protected float[] readVector(IdLexer lexer, int n) {
        var values = new float[n];
        if (!lexer.checkPunctuation(LexerPunctuation.P_BRACEOPEN)) {
            var number = lexer.parseFloat();
            Arrays.fill(values, number);
            return values;
        }

        values[0] = lexer.parseFloat();
        while (!lexer.checkPunctuation(LexerPunctuation.P_BRACECLOSE)) {
            for (var i = 1; i < n; i++) {
                if (lexer.peekPunctuation(LexerPunctuation.P_BRACECLOSE)) {
                    break;
                }
                lexer.expectPunctuation(LexerPunctuation.P_COMMA);
                values[i] = lexer.parseFloat();
            }
        }
        return values;
    }

    protected String parseType(IdLexer lexer) {
        lexer.expectPunctuation(LexerPunctuation.P_BRACEOPEN);

        var builder = new StringBuilder("struct\n{\n\t");
        while (true) {
            var token = lexer.expectAnyToken();
            if (token.isPunctuation(LexerPunctuation.P_BRACECLOSE)) {
                break;
            }
            if (token.isPunctuation(LexerPunctuation.P_SEMICOLON)) {
                builder.setLength(builder.length() - 1);
                builder.append(";\n\t");
                continue;
            }
            builder.append(token.value());
            // The engine glues '#' to the next token, so '#define' stays intact
            if (!token.isPunctuation(LexerPunctuation.P_PRECOMP)) {
                builder.append(" ");
            }
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

    protected ImageProperties parseImageProperties(IdLexer lexer) {
        var result = new ImageProperties();
        while (true) {
            IdToken peek = lexer.peekToken();
            if (peek == null || peek.type() != TokenType.TT_NAME) {
                return result;
            }
            String name = peek.value();
            if (Skipped.contains(name)) {
                lexer.expectName();
                continue;
            }

            TextureFormat textureFormat = TextureFormat.parse(name);
            if (textureFormat != null) {
                result.format = textureFormat;
                lexer.expectName();
                continue;
            }

            Integer padding = parsePadding(name);
            if (padding != null) {
                result.padding = padding;
                lexer.expectName();
                continue;
            }

            switch (name) {
                case "fullscalebias" -> {
                    result.fullScaleBias = true;
                    lexer.expectName();
                    continue;
                }
                case "fftbloom" -> {
                    result.fftBloom = true;
                    lexer.expectName();
                    continue;
                }
                case "nomips" -> {
                    result.noMips = true;
                    lexer.expectName();
                    continue;
                }
                case "streamed" -> {
                    result.streamed = true;
                    lexer.expectName();
                    continue;
                }
            }

            break;
        }
        return result;
    }

    protected ParmScope parseParmScope(IdLexer lexer) {
        var token = peekValue(lexer).toLowerCase();
        var parmScope = switch (token) {
            case "view" -> ParmScope.PSCP_VIEW;
            case "instance" -> ParmScope.PSCP_INSTANCE;
            case "surface" -> ParmScope.PSCP_SURFACE;
            default -> null;
        };
        if (parmScope != null) {
            lexer.expectName();
        }
        return parmScope;
    }

    protected ParmEdit parseParmEdit(IdLexer lexer) {
        String token = peekValue(lexer).toLowerCase();
        var parmEdit = switch (token) {
            case "bool" -> new ParmEdit.Bool();
            case "srgb", "srgba" -> new ParmEdit.Srgba();
            case "color" -> new ParmEdit.Color();
            case "range" -> {
                lexer.expectName();
                Number min = lexer.parseFloat();
                lexer.expectPunctuation(LexerPunctuation.P_COMMA);
                Number max = lexer.parseFloat();
                yield new ParmEdit.Range(min, max);
            }
            default -> null;
        };
        if (parmEdit != null && !(parmEdit instanceof ParmEdit.Range)) {
            lexer.expectName();
        }
        return parmEdit;
    }

    protected boolean parseFlags(IdLexer lexer, RenderParm result) {
        if (lexer.peekPunctuation(LexerPunctuation.P_BRACECLOSE)) {
            return false;
        }

        var read = switch (peekValue(lexer).toLowerCase()) {
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
            lexer.expectName();
        }
        return read;
    }

    protected void parseExtras(IdLexer lexer, RenderParm result) {
        while (!lexer.peekPunctuation(LexerPunctuation.P_BRACECLOSE)) {
            var edit = parseParmEdit(lexer);
            if (edit != null) {
                result.parmEdit = edit;
                continue;
            }

            var scope = parseParmScope(lexer);
            if (scope != null) {
                result.parmScope = scope;
                continue;
            }

            var flags = parseFlags(lexer, result);
            if (flags) {
                continue;
            }

            var token = lexer.expectName().value().toLowerCase();
            switch (token) {
                case "materialkind" -> {
                    lexer.expectPunctuation(LexerPunctuation.P_ASSIGN);
                    result.materialKind = TextureMaterialKind.parse(lexer.expectName().value());
                    continue;
                }
                case "smoothnessnormalparm" -> {
                    lexer.expectPunctuation(LexerPunctuation.P_ASSIGN);
                    result.smoothnessNormalParm = TextureMaterialKind.parse(lexer.expectName().value());
                    continue;
                }
                case "autobind" -> {
                    // ignore
                    if (lexer.checkPunctuation(LexerPunctuation.P_BRACEOPEN)) {
                        lexer.expectName();
                        lexer.expectPunctuation(LexerPunctuation.P_BRACECLOSE);
                    }
                    continue;
                }
                case "cvar", "const", "coherent" -> {
                    // ignore
                    continue;
                }
            }

            throw new IllegalArgumentException("Unsupported parm type: " + token);
        }
    }

    protected RenderParm read(BinarySource source) throws IOException {
        var string = source.readString(Math.toIntExact(source.size()));
        var lexer = new IdLexer(string,
            LexerFlags.LEXFL_NOSTRINGCONCAT,
            LexerFlags.LEXFL_NOSTRINGESCAPECHARS,
            LexerFlags.LEXFL_ALLOWPATHNAMES,
            LexerFlags.LEXFL_ALLOWMULTICHARLITERALS,
            LexerFlags.LEXFL_ALLOWBACKSLASHSTRINGCONCAT,
            LexerFlags.LEXFL_ALLOWRAWSTRINGBLOCKS
        );

        var result = new RenderParm();
        lexer.expectPunctuation(LexerPunctuation.P_BRACEOPEN);
        result.parmType = ParmType.parse(lexer.expectName().value());

        result.declaredValue = parseValue(lexer, result);
        if (result.declaredValue instanceof ImageProperties imageProperties) {
            result.streamed = imageProperties.streamed;
        }

        parseExtras(lexer, result);
        lexer.expectPunctuation(LexerPunctuation.P_BRACECLOSE);
        return result;
    }

    private String expectNameOrString(IdLexer lexer) {
        var token = lexer.expectAnyToken();
        if (token.type() != TokenType.TT_NAME && token.type() != TokenType.TT_STRING) {
            throw new IllegalArgumentException("Expected a name or string, but got " + token.value());
        }
        return token.value();
    }

    private String peekValue(IdLexer lexer) {
        var token = lexer.peekToken();
        return token != null ? token.value() : "";
    }

    private boolean readBool(IdLexer lexer) {
        var value = lexer.expectAnyToken().value();
        if (value.equalsIgnoreCase("true") || value.equals("1")) {
            return true;
        }
        if (value.equalsIgnoreCase("false") || value.equals("0")) {
            return false;
        }
        throw new IllegalArgumentException("Expected a boolean, but got " + value);
    }

    private int readInt(IdLexer lexer) {
        var token = lexer.expectAnyToken();
        boolean negative = token.isPunctuation(LexerPunctuation.P_SUB);
        if (negative) {
            token = lexer.expectTokenType(TokenType.TT_NUMBER, 0);
        } else if (token.type() != TokenType.TT_NUMBER) {
            throw new IllegalArgumentException("Expected a number, but got " + token.value());
        }
        int value = (int) token.getLongValue();
        return negative ? -value : value;
    }
}
