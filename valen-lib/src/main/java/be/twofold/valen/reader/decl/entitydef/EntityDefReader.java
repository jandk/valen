package be.twofold.valen.reader.decl.entitydef;

import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.decl.parser.*;
import be.twofold.valen.resource.*;
import com.google.gson.*;
import jakarta.inject.*;

import java.io.*;

public final class EntityDefReader implements ResourceReader<JsonObject> {

    @Inject
    public EntityDefReader() {
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.RsStreamFile
            && entry.nameString().startsWith("generated/decls/entitydef/");
    }

    @Override
    public JsonObject read(DataSource source, Resource resource) throws IOException {
        var bytes = source.readBytes(Math.toIntExact(source.size()));
        var parser = new DeclParser(new String(bytes), true, true);
        return parseObject(parser);
    }

    private JsonObject parseObject(DeclParser parser) {
        var object = new JsonObject();
        parser.expect(DeclTokenType.OpenBrace);
        while (parser.peek().type() != DeclTokenType.CloseBrace) {
            var key = parser.expectName();
            if (parser.peek().type() == DeclTokenType.Assign) {
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
        var peeked = parser.peek();
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
        if (parser.peek().type() != DeclTokenType.CloseParen) {
            while (true) {
                arr.add(parser.expectNumber());
                if (parser.peek().type() == DeclTokenType.CloseParen) {
                    break;
                }
                parser.expect(DeclTokenType.Comma);
            }
        }
        parser.expect(DeclTokenType.CloseParen);
        return arr;
    }
}
