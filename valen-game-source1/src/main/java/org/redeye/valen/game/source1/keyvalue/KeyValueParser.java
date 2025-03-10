package org.redeye.valen.game.source1.keyvalue;

import be.twofold.valen.core.util.*;

import java.util.*;

public final class KeyValueParser {
    private final KeyValueLexer lexer;

    public KeyValueParser(KeyValueLexer lexer) {
        this.lexer = Check.notNull(lexer, "lexer");
    }

    public KeyValue.Obj parse() {
        var key = expectString();
        if (key.equals("#include")) {
            var filename = expectString();
            System.out.println("#include " + filename);
            key = expectString();
        } else if (key.equals("#base")) {
            var filename = expectString();
            System.out.println("#base " + filename);
            key = expectString();
        }
        expect(KeyValueTokenType.OpenBrace);
        var value = parseObject();
        return new KeyValue.Obj(List.of(Map.entry(key, value)));
    }

    private KeyValue.Obj parseObject() {
        var entries = new ArrayList<Map.Entry<String, KeyValue>>();
        while (true) {
            String key;
            var keysToken = lexer.nextToken();
            switch (keysToken.type()) {
                case CloseBrace:
                    return new KeyValue.Obj(entries);
                case String:
                    key = keysToken.value();
                    break;
                default:
                    throw new IllegalStateException("Expected '}' or String, got " + keysToken);
            }

            var valueToken = lexer.nextToken();
            KeyValue value = switch (valueToken.type()) {
                case OpenBrace -> parseObject();
                case String -> new KeyValue.Str(valueToken.value());
                default -> throw new IllegalStateException("Expected '{' or String, got " + valueToken);
            };

            entries.add(Map.entry(key, value));
        }
    }

    private KeyValueToken expect(KeyValueTokenType type) {
        var token = lexer.nextToken();
        if (token.type() != type) {
            throw new IllegalStateException("Expecting " + type + " but got " + token.type());
        }
        return token;
    }

    private String expectString() {
        return expect(KeyValueTokenType.String).value();
    }
}
