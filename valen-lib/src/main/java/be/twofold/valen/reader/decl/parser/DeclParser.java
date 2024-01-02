package be.twofold.valen.reader.decl.parser;

import com.google.gson.*;

public final class DeclParser {
    private final DeclLexer lexer;

    public DeclParser(String source) {
        this.lexer = new DeclLexer(source);
    }

    public static JsonObject parse(String source) {
        return new DeclParser(source).parse();
    }

    public void expect(DeclTokenType type) {
        doExpect(type);
    }

    public String expectName() {
        return doExpect(DeclTokenType.Name).value();
    }

    public Number expectNumber() {
        var token = doExpect(DeclTokenType.Number);
        return new StringNumber(token.value());
    }

    public String expectString() {
        return doExpect(DeclTokenType.String).value();
    }

    private DeclToken doExpect(DeclTokenType type) {
        var token = lexer.nextToken();
        if (token.type() != type) {
            throw new DeclParseException("Expected " + type + ", got " + token);
        }
        return token;
    }

    public void expect(DeclTokenType type, String value) {
        var token = lexer.nextToken();
        if (token.type() != type || !token.value().equals(value)) {
            throw new DeclParseException("Expected " + type + " " + value + ", got " + token);
        }
    }

    public boolean isEof() {
        return lexer.peekToken().type() == DeclTokenType.Eof;
    }

    public DeclToken peekToken() {
        return lexer.peekToken();
    }

    public JsonObject parse() {
        JsonObject result;
        try {
            test(lexer.nextToken(), DeclTokenType.OpenBrace);
            result = parseObject();
        } catch (StackOverflowError e) {
            throw new DeclParseException("Stack overflow");
        }
        var token = lexer.nextToken();
        if (token.type() != DeclTokenType.Eof) {
            throw new DeclParseException("Not a single DECL document");
        }
        return result;
    }

    public JsonElement parseValue() {
        DeclToken token = lexer.nextToken();
        return switch (token.type()) {
            case OpenBrace -> parseObject();
            case String -> new JsonPrimitive(token.value());
            case Number -> new JsonPrimitive(new StringNumber(token.value()));
            case Name -> switch (token.value()) {
                case "true" -> new JsonPrimitive(true);
                case "false" -> new JsonPrimitive(false);
                case "NULL" -> JsonNull.INSTANCE;
                default -> new JsonPrimitive(token.value());
            };
            default -> throw new DeclParseException("Unexpected " + token);
        };
    }

    private JsonObject parseObject() {
        var object = new JsonObject();
        while (true) {
            DeclToken token = lexer.nextToken();
            if (token.type() == DeclTokenType.CloseBrace) {
                break;
            }
            var key = test(token, DeclTokenType.Name).value();

            // TODO: Extract function
            token = lexer.nextToken();
            if (token.type() == DeclTokenType.OpenBracket) {
                var index = lexer.nextToken();
                test(index, DeclTokenType.Number);
                test(lexer.nextToken(), DeclTokenType.CloseBracket);
                key += "[" + index.value() + "]";
                token = lexer.nextToken();
            }
            test(token, DeclTokenType.Assign);

            var value = parseValue();

            // TODO: Extract function
            if (value instanceof JsonObject) {
                token = lexer.peekToken();
                if (token.type() == DeclTokenType.Semicolon) {
                    lexer.nextToken();
                }
            } else {
                test(lexer.nextToken(), DeclTokenType.Semicolon);
            }
            object.add(key, value);
        }
        return object;
    }

    private DeclToken test(DeclToken actual, DeclTokenType expected) {
        if (actual.type() != expected) {
            throw new DeclParseException("Expected " + expected + ", got " + actual.type());
        }
        return actual;
    }
}
