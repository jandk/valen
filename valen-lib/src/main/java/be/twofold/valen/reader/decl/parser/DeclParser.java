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
            test(lexer.nextToken(), DeclTokenType.ObjectStart);
            result = parseObject();
        } catch (StackOverflowError e) {
            throw new RuntimeException("Stack overflow");
        }
        DeclToken token = lexer.nextToken();
        if (token.type() != DeclTokenType.Eof) {
            throw new RuntimeException("Not a single DECL document");
        }
        return result;
    }

    private DeclValue parseValue() {
        DeclToken token = lexer.nextToken();
        return switch (token.type()) {
            case ObjectStart -> parseObject();
            case String -> new DeclString(token.value());
            case Number -> new DeclNumber(token.value());
            case True -> DeclBoolean.True;
            case False -> DeclBoolean.False;
            case Null -> DeclNull.Null;
            default -> throw new RuntimeException("Unexpected " + token);
        };
    }

    private DeclObject parseObject() {
        DeclObject object = new DeclObject();
        while (true) {
            DeclToken token = lexer.nextToken();
            if (token.type() == DeclTokenType.ObjectEnd) {
                break;
            }
            String key = test(token, DeclTokenType.Name).value();
            test(lexer.nextToken(), DeclTokenType.Equals);

            DeclValue value = parseValue();
            if (lexer.peekToken().type() == DeclTokenType.Semicolon) {
                lexer.nextToken();
            }
            object.put(key, value);
        }
        return object;
    }

    private DeclToken test(DeclToken actual, DeclTokenType expected) {
        if (actual.type() != expected) {
            throw new RuntimeException("Expected " + expected + ", got " + actual);
        }
        return actual;
    }
}
