package be.twofold.valen.reader.decl.parser;

import be.twofold.valen.reader.decl.model.*;

public final class DeclParser {
    private final DeclLexer lexer;

    public DeclParser(String source) {
        this.lexer = new DeclLexer(source);
    }

    public DeclValue parse() {
        DeclValue result;
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

    private DeclValue parseValue() {
        var token = lexer.nextToken();
        return switch (token.type()) {
            case OpenBrace -> parseObject();
            case String -> new DeclString(token.value());
            case Number -> new DeclNumber(token.value());
            case Name -> switch (token.value()) {
                case "true" -> DeclBoolean.True;
                case "false" -> DeclBoolean.False;
                case "NULL" -> DeclNull.Null;
                default -> new DeclString(token.value());
            };
            default -> throw new DeclParseException("Unexpected " + token);
        };
    }

    private DeclObject parseObject() {
        var object = new DeclObject();
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
            if (value instanceof DeclObject) {
                token = lexer.peekToken();
                if (token.type() == DeclTokenType.Semicolon) {
                    lexer.nextToken();
                }
            } else {
                test(lexer.nextToken(), DeclTokenType.Semicolon);
            }
            object.put(key, value);
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
