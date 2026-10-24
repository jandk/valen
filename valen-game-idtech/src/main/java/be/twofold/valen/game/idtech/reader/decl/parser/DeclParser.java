package be.twofold.valen.game.idtech.reader.decl.parser;

import be.twofold.valen.game.idtech.lexer.*;
import com.google.gson.*;
import wtf.reversed.toolbox.util.*;

public final class DeclParser {
    private final IdLexer lexer;

    public DeclParser(IdLexer lexer) {
        this.lexer = Check.nonNull(lexer, "lexer");
    }

    public static IdLexer createLexer(String source) {
        return new IdLexer(source,
            LexerFlags.LEXFL_NOFATALERRORS,
            LexerFlags.LEXFL_NOSTRINGCONCAT,
            LexerFlags.LEXFL_ALLOWFLOATEXCEPTIONS,
            LexerFlags.LEXFL_ALLOWRAWSTRINGBLOCKS
        );
    }

    public static JsonObject parse(String source) {
        var lexer = createLexer(source);
        lexer.expectPunctuation(LexerPunctuation.P_BRACEOPEN);
        var result = new DeclParser(lexer).parseObject();
        var token = lexer.readToken();
        if (token != null) {
            throw new DeclParseException("Not a single DECL document, found '" + token.value() + "'");
        }
        return result;
    }

    public JsonObject parseObject() {
        var object = new JsonObject();
        while (!lexer.checkPunctuation(LexerPunctuation.P_BRACECLOSE)) {
            var key = lexer.expectAnyToken().value();
            if (lexer.checkPunctuation(LexerPunctuation.P_SQBRACKETOPEN)) {
                var index = lexer.parseInt();
                lexer.expectPunctuation(LexerPunctuation.P_SQBRACKETCLOSE);
                key += "[" + index + "]";
            }
            lexer.expectPunctuation(LexerPunctuation.P_ASSIGN);

            var value = parseValue();
            if (value instanceof JsonObject) {
                if (!lexer.checkPunctuation(LexerPunctuation.P_SEMICOLON)) {
                    lexer.checkPunctuation(LexerPunctuation.P_COMMA);
                }
            } else {
                lexer.expectPunctuation(LexerPunctuation.P_SEMICOLON);
            }
            object.add(key, value);
        }
        return object;
    }

    public JsonElement parseValue() {
        var token = lexer.expectAnyToken();
        return switch (token.type()) {
            case TT_STRING, TT_LITERAL -> new JsonPrimitive(token.value());
            case TT_NUMBER -> new JsonPrimitive(new StringNumber(token.value()));
            case TT_NAME -> switch (token.value()) {
                case "true" -> new JsonPrimitive(true);
                case "false" -> new JsonPrimitive(false);
                case "NULL" -> JsonNull.INSTANCE;
                default -> new JsonPrimitive(token.value());
            };
            case TT_PUNCTUATION -> {
                if (token.isPunctuation(LexerPunctuation.P_BRACEOPEN)) {
                    yield parseObject();
                }

                if (token.isPunctuation(LexerPunctuation.P_SUB)) {
                    var number = lexer.expectTokenType(TokenType.TT_NUMBER, 0);
                    yield new JsonPrimitive(new StringNumber("-" + number.value()));
                }
                throw new DeclParseException("Unexpected '" + token.value() + "'");
            }
        };
    }
}
