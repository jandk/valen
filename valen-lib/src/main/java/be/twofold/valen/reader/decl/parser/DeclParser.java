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
            while (true) {
                if (lexer.nextToken().type() == DeclTokenType.ObjectStart) {
                    break;
                }
            }
            result = parseObject();
        } catch (StackOverflowError e) {
            throw new RuntimeException("Stack overflow");
        }
        DeclToken token = lexer.nextToken();
        if (token.type() != DeclTokenType.Eof) {
            throw new RuntimeException("Not a single JSON document");
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
            if (object.size() > 0) {
                if (token.type() == DeclTokenType.Semicolon) {
                    token = lexer.nextToken();
                }
                if (token.type() == DeclTokenType.ObjectEnd) {
                    break;
                }
            }
            if (token.type() != DeclTokenType.String) {
                throw new RuntimeException("Expected string, got " + token);
            }
            String key = token.value();
            if ("entityDef".equals(key)) {
                key = "entityDef--" + lexer.nextToken().value();
            }
            if (lexer.peekToken().type() == DeclTokenType.Equals) {
                lexer.nextToken();
            }
            DeclValue value = parseValue();
            object.put(key, value);
        }
        return object;
    }
}
