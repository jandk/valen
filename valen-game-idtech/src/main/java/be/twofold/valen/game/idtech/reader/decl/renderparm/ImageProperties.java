package be.twofold.valen.game.idtech.reader.decl.renderparm;

import be.twofold.valen.game.idtech.lexer.*;
import be.twofold.valen.game.idtech.reader.image.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public record ImageProperties(
    String name,
    TextureFormat format,
    int padding,
    Set<Flags> flags
) {
    private static final Set<String> SKIPPED = Set.of(
        "borderclamp",
        "clamp",
        "clamps",
        "clampt",
        "linear",
        "mirror",
        "nearest"
    );

    public ImageProperties {
        Check.nonNull(name, "name");
        Check.nonNull(format, "format");
        var flagsCopy = EnumSet.noneOf(Flags.class);
        flagsCopy.addAll(flags);
        flags = Collections.unmodifiableSet(flagsCopy);
    }

    public static ImageProperties parse(IdLexer lexer) {
        var format = TextureFormat.FMT_NONE;
        var padding = 0;
        var flags = EnumSet.noneOf(Flags.class);
        while (true) {
            var peek = lexer.peekToken();
            if (peek == null || peek.type() != TokenType.TT_NAME) {
                break;
            }
            var option = peek.value();
            if (SKIPPED.contains(option)) {
                lexer.expectName();
                continue;
            }

            var textureFormat = TextureFormat.parse(option);
            if (textureFormat != null) {
                format = textureFormat;
                lexer.expectName();
                continue;
            }

            var parsedPadding = parsePadding(option);
            if (parsedPadding != null) {
                padding = parsedPadding;
                lexer.expectName();
                continue;
            }

            var flag = parseImageFlag(option);
            if (flag != null) {
                flags.add(flag);
                lexer.expectName();
                continue;
            }

            break;
        }
        var name = expectNameOrString(lexer);
        return new ImageProperties(name, format, padding, flags);
    }

    private static Integer parsePadding(String token) {
        return switch (token) {
            case "pad2" -> 2;
            case "pad4" -> 4;
            case "pad8" -> 8;
            case "pad16" -> 16;
            default -> null;
        };
    }

    private static Flags parseImageFlag(String token) {
        return switch (token) {
            case "fftbloom" -> Flags.FFT_BLOOM;
            case "fullscalebias" -> Flags.FULL_SCALE_BIAS;
            case "nomips" -> Flags.NO_MIPS;
            case "streamed" -> Flags.STREAMED;
            default -> null;
        };
    }

    private static String expectNameOrString(IdLexer lexer) {
        var token = lexer.expectAnyToken();
        if (token.type() != TokenType.TT_NAME && token.type() != TokenType.TT_STRING) {
            throw new IllegalArgumentException("Expected a name or string, but got " + token.value());
        }
        return token.value();
    }

    public enum Flags {
        FFT_BLOOM,
        FULL_SCALE_BIAS,
        NO_MIPS,
        STREAMED,
    }
}
