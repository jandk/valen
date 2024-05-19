package be.twofold.valen.reader.decl.parser;

import com.google.gson.*;

import java.util.function.*;


public final class DeclParser {
    private final DeclLexer lexer;
    private final boolean lenient;

    public DeclParser(String source) {
        this(source, false, true);
    }

    public DeclParser(String source, boolean lenient, boolean skipNewlines) {
        this.lexer = new DeclLexer(source, lenient, skipNewlines);
        this.lenient = lenient;
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

    public boolean expectBoolean() {
        var name = expectName();
        return switch (name) {
            case "true" -> true;
            case "false" -> false;
            default -> throw new DeclParseException("Expected boolean, got " + name);
        };
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

    public DeclToken next() {
        return lexer.nextToken();
    }

    public DeclToken peek() {
        return lexer.peekToken();
    }

    public String peekName() {
        var token = lexer.peekToken();
        if (token.type() != DeclTokenType.Name) {
            throw new DeclParseException("Expected " + DeclTokenType.Name + ", got " + token);
        }
        return token.value();
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
            case OpenParen -> parseArray();
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
        while (!match(DeclTokenType.CloseBrace)) {
            var key = expectName();
            if (match(DeclTokenType.OpenBracket)) {
                var index = expectNumber().intValue();
                key += "[" + index + "]";
                expect(DeclTokenType.CloseBracket);
            }

            if (lenient) {
                match(DeclTokenType.Assign);
            } else {
                expect(DeclTokenType.Assign);
            }

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

    private JsonArray parseArray() {
        if (!lenient) {
            throw new DeclParseException("Arrays are not allowed in strict mode");
        }
        var array = new JsonArray();
        while (!match(DeclTokenType.CloseParen)) {
            if (!array.isEmpty()) {
                expect(DeclTokenType.Comma);
            }
            array.add(parseValue());
        }
        return array;
    }

    public JsonArray collectUntil(Supplier<JsonElement> supplier, DeclTokenType terminator) {
        var arr = new JsonArray();
        if (terminator != DeclTokenType.NewLine) {
            skipNewLines();
        }
        while (peek().type() != terminator) {
            arr.add(supplier.get());
            if (terminator != DeclTokenType.NewLine) {
                skipNewLines();
            }
        }
        expect(terminator);
        return arr;
    }

    public void runUntil(Runnable runnable, DeclTokenType terminator) {
        if (terminator != DeclTokenType.NewLine) {
            skipNewLines();
        }
        while (peek().type() != terminator) {
            runnable.run();
            if (terminator != DeclTokenType.NewLine) {
                skipNewLines();
            }
        }
        expect(terminator);
    }

    private void skipNewLines() {
        while (peek().type() == DeclTokenType.NewLine) {
            expect(DeclTokenType.NewLine);
        }
    }
}
