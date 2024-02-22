package be.twofold.valen.reader.decl.entitydef;

import be.twofold.valen.reader.decl.parser.DeclParser;
import be.twofold.valen.reader.decl.parser.DeclTokenType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class EntityDefParser {

    public JsonObject parse(String source) {

        return parseObject(new DeclParser(source));
    }

    private JsonObject parseObject(DeclParser parser) {
        var object = new JsonObject();
        parser.expect(DeclTokenType.OpenBrace);
        while (parser.peekToken().type() != DeclTokenType.CloseBrace) {
            var key = parser.expectName();
            if (parser.peekToken().type() == DeclTokenType.Assign) {
                parser.expect(DeclTokenType.Assign);
                object.add(key, parseValue(parser, key));
            } else {
                object.add(key, parseValue(parser, key));
            }
        }
        parser.expect(DeclTokenType.CloseBrace);
        return object;
    }

    private JsonElement parseValue(DeclParser parser, String key) {
        if (key.equals("edit")) {
            parser.expect(DeclTokenType.OpenBrace);
            return parser.parseObject();
        }
        var peeked = parser.peekToken();
        switch (peeked.type()) {
            case OpenParen -> {
                JsonArray value = parseArray(parser);
                parser.expect(DeclTokenType.Semicolon);
                return value;
            }
            case OpenBrace -> {
                return parseObject(parser);
            }
            case String -> {
                JsonPrimitive value = new JsonPrimitive(parser.expectString());
                parser.expect(DeclTokenType.Semicolon);
                return value;
            }
            case Number -> {
                JsonPrimitive value = new JsonPrimitive(parser.expectNumber());
                parser.expect(DeclTokenType.Semicolon);
                return value;
            }
            case Name -> {
                JsonPrimitive value = new JsonPrimitive(parser.expectName());
                parser.expect(DeclTokenType.Semicolon);
                return value;
            }
            default -> throw new IllegalStateException("Unexpected value: " + peeked);
        }
    }

    private JsonArray parseArray(DeclParser parser) {
        parser.expect(DeclTokenType.OpenParen);
        var arr = new JsonArray();
        if (parser.peekToken().type() != DeclTokenType.CloseParen) {
            while (true) {
                arr.add(parser.expectNumber());
                if (parser.peekToken().type() == DeclTokenType.CloseParen) {
                    break;
                }
                parser.expect(DeclTokenType.Comma);
            }
        }
        parser.expect(DeclTokenType.CloseParen);
        return arr;
    }
}
