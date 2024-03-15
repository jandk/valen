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


    public DeclToken expect(DeclTokenType type) {
        var token = lexer.nextToken();
        if (token.type() != type) {
            throw new DeclParseException("Expected " + type + ", got " + token);
        }
        return token;
    }

    public String expectName() {
        return expect(DeclTokenType.Name).value();
    }

    public void expectName(String value) {
        var token = lexer.nextToken();
        if (token.type() != DeclTokenType.Name || !token.value().equals(value)) {
            throw new DeclParseException("Expected " + DeclTokenType.Name + " " + value + ", got " + token);
        }
    }

    public Number expectNumber() {
        var value = expect(DeclTokenType.Number).value();
        return new StringNumber(value);
    }

    public String expectString() {
        return expect(DeclTokenType.String).value();
    }


    public boolean match(DeclTokenType type) {
        if (lexer.peekToken().type() == type) {
            lexer.nextToken();
            return true;
        }
        return false;
    }

    public boolean matchName(String value) {
        var token = lexer.peekToken();
        if (token.type() == DeclTokenType.Name && token.value().equals(value)) {
            lexer.nextToken();
            return true;
        }
        return false;
    }


    public JsonObject parse() {
        JsonObject result;
        try {
            expect(DeclTokenType.OpenBrace);
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
            if (match(DeclTokenType.CloseBrace)) {
                break;
            }

            var key = expectName();
            if (match(DeclTokenType.OpenBracket)) {
                var index = expectNumber().intValue();
                key += "[" + index + "]";
                expect(DeclTokenType.CloseBracket);
            }
            expect(DeclTokenType.Assign);

            var value = parseValue();

            if (value instanceof JsonObject) {
                var token = lexer.peekToken();
                if (token.type() == DeclTokenType.Semicolon || token.type() == DeclTokenType.Comma) {
                    lexer.nextToken();
                }
            } else {
                expect(DeclTokenType.Semicolon);
            }
            object.add(key, value);
        }
        return object;
    }

}
