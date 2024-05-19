package be.twofold.valen.reader.decl.md6def;

import be.twofold.valen.reader.decl.parser.*;
import com.google.gson.*;

public class MD6DefParser {
    public JsonObject parse(String source) {
        var parser = new DeclParser(source, true, false);

        return parseDef(parser);
    }

    private JsonObject parseDef(DeclParser parser) {
        parser.expect(DeclTokenType.OpenBrace);
        var def = new JsonObject();
        parser.runUntil(() -> {
            String key = parser.expectName();
            JsonElement value = switch (key) {
                case "init" -> parseObject(parser);
                case "userProps" -> parseObject(parser);
                case "jointGroups" -> parseJointGroups(parser);
                case "events" -> parseAnimations(parser);
                case "aliases" -> parseAliases(parser);
                case "props" -> parseProps(parser);
                case "eyeInfoCollection" -> parseEyeInfoCollection(parser);
                case "meshKits" -> parseMeshKits(parser);
                case "userChannelWeightGroupOverride" -> parseUserChannelWeightGroupOverride(parser);
                case "discreteCollection" -> parseObject(parser);
                case "rigs" -> parser.collectUntil(() -> {
                    parser.expect(DeclTokenType.OpenBrace);
                    return new JsonPrimitive(parser.expectString());
                }, DeclTokenType.CloseBrace);
                default -> throw new UnsupportedOperationException("Unexpected key: " + key);
            };
            def.add(key, value);
        }, DeclTokenType.CloseBrace);

        return def;
    }

    private JsonArray parseUserChannelWeightGroupOverride(DeclParser parser) {
        parser.expect(DeclTokenType.OpenBrace);
        return parser.collectUntil(() -> new JsonPrimitive(parser.expectString()), DeclTokenType.CloseBrace);
    }

    private JsonArray parseMeshKits(DeclParser parser) {
        parser.expect(DeclTokenType.OpenBrace);
        return parser.collectUntil(() -> parseMeshKit(parser), DeclTokenType.CloseBrace);
    }

    private JsonObject parseMeshKit(DeclParser parser) {
        var obj = new JsonObject();
        obj.addProperty("name", parser.expectName());
        obj.addProperty("count", parser.expectNumber());
        var parts = new JsonObject();
        parser.expect(DeclTokenType.OpenBrace);
        parser.runUntil(() -> {
            var key = parser.expectString();
            parser.expect(DeclTokenType.Assign);
            parts.addProperty(key, parser.expectString());
        }, DeclTokenType.CloseBrace);
        return obj;
    }

    private JsonObject parseEyeInfoCollection(DeclParser parser) {
        var obj = new JsonObject();
        obj.addProperty("unkInt", parser.expectNumber());
        parser.expect(DeclTokenType.OpenBrace);
        parser.expect(DeclTokenType.CloseBrace);
        return obj;
    }

    private JsonArray parseProps(DeclParser parser) {
        parser.expect(DeclTokenType.OpenBrace);
        return parser.collectUntil(() -> parseProp(parser), DeclTokenType.CloseBrace);
    }

    private JsonObject parseProp(DeclParser parser) {
        var prop = new JsonObject();
        parser.expectName("prop");
        prop.addProperty("name", parser.expectString());
        var propProperties = new JsonObject();
        parser.expect(DeclTokenType.OpenBrace);
        parser.runUntil(() -> {
            var key = parser.expectName();
            var value = switch (key) {
                case "visibleByDefault" -> new JsonPrimitive(parser.expectName().equals("true"));
                case "tag" -> parseTag(parser);
                default -> throw new IllegalStateException("Unexpected value: " + key);
            };
            propProperties.add(key, value);
        }, DeclTokenType.CloseBrace);
        prop.add("properties", propProperties);
        return prop;
    }

    private JsonObject parseTag(DeclParser parser) {
        var tag = new JsonObject();
        tag.addProperty("name", parser.expectString());
        var tagData = parseObject(parser);
        tagData.entrySet().forEach(entry -> tag.add(entry.getKey(), entry.getValue()));
        return tag;
    }

    private JsonArray parseAliases(DeclParser parser) {
        parser.expect(DeclTokenType.OpenBrace);
        var arr = new JsonArray();
        arr.addAll(parser.collectUntil(() -> parseAlias(parser), DeclTokenType.CloseBrace));
        return arr;
    }

    private JsonObject parseAlias(DeclParser parser) {
        parser.expectName("alias");
        parser.expect(DeclTokenType.OpenBrace);
        var alias = new JsonObject();
        parser.runUntil(() -> {
            var key = parser.expectName();
            JsonElement value = switch (key) {
                case "name", "anim" -> new JsonPrimitive(parser.expectString());
                case "flags" -> {
                    parser.expect(DeclTokenType.OpenBrace);
                    var arr = new JsonArray();
                    arr.add(parser.collectUntil(() -> new JsonPrimitive(parser.expectName()), DeclTokenType.CloseBrace));
                    yield arr;
                }
                default -> throw new UnsupportedOperationException("Unexpected key: " + key);
            };
            alias.add(key, value);
        }, DeclTokenType.CloseBrace);
        return alias;
    }

    private JsonArray parseAnimations(DeclParser parser) {
        parser.expect(DeclTokenType.OpenBrace);
        return parser.collectUntil(() -> parseAnimEvents(parser), DeclTokenType.CloseBrace);
    }

    private JsonObject parseAnimEvents(DeclParser parser) {
        var obj = new JsonObject();
        var type = parser.expectName();
        var name = parser.expectString();
        obj.addProperty("type", type);
        obj.addProperty("name", name);
        parser.expect(DeclTokenType.OpenBrace);
        parser.collectUntil(() -> {
            var key = parser.expectName();
            return switch (key) {
                case "event" -> parseEvent(parser);
                default -> throw new UnsupportedOperationException(key);
            };
        }, DeclTokenType.CloseBrace);
        return obj;
    }

    private JsonObject parseEvent(DeclParser parser) {
        var event = new JsonObject();
        event.addProperty("type", "event");
        event.addProperty("name", parser.expectString());
        var eventInfo = parseObject(parser);
        event.add("data", eventInfo);
        return event;
    }

    private JsonObject parseJointGroups(DeclParser parser) {
        var groups = new JsonObject();


        parser.expect(DeclTokenType.OpenBrace);
        parser.runUntil(() -> {
            var groupType = parser.expectName();
            var groupName = parser.expectString();
            JsonObject groupCollection = (JsonObject) groups.asMap().computeIfAbsent(groupType, s -> {
                JsonObject col = new JsonObject();
                groups.add(s, col);
                return col;
            });
            JsonArray groupData;
            if (parser.peek().type() == DeclTokenType.OpenBrace) {
                parser.expect(DeclTokenType.OpenBrace);
                groupData = parser.collectUntil(() -> {
                    var name = parser.expectName();
                    JsonObject data;
                    if (parser.peek().type() == DeclTokenType.OpenBrace) {
                        data = parseObject(parser);
                    } else {
                        data = new JsonObject();
                    }
                    data.addProperty("name", name);
                    return data;
                }, DeclTokenType.CloseBrace);
            } else if (parser.peek().type() == DeclTokenType.Assign) {
                parser.expect(DeclTokenType.Assign);
                var parentType = parser.expectName();
                var parentName = parser.expectString();
                groupData = groups.getAsJsonObject(parentType).getAsJsonArray(parentName);
            } else {
                throw new UnsupportedOperationException();
            }
            groupCollection.add(groupName, groupData);
        }, DeclTokenType.CloseBrace);

        return groups;
    }

    private JsonArray parseFloatArray(DeclParser parser) {
        parser.expect(DeclTokenType.OpenParen);
        return parser.collectUntil(() -> new JsonPrimitive(parser.expectNumber()), DeclTokenType.CloseParen);
    }

    private JsonObject parseObject(DeclParser parser) {
        var obj = new JsonObject();
        parser.expect(DeclTokenType.OpenBrace);
        parser.runUntil(() -> {
            var key = parser.expectName();
            JsonElement value = switch (parser.peek().type()) {
                case String -> {
                    var res = parser.collectUntil(() -> {
                        var item = new JsonPrimitive(parser.expectString());
                        if (parser.peek().type() == DeclTokenType.Comma) {
                            parser.expect(DeclTokenType.Comma);
                        }
                        return item;
                    }, DeclTokenType.NewLine);
                    if (res.size() == 1) {
                        yield res.get(0);
                    }
                    yield res;
                }
                case Number -> new JsonPrimitive(parser.expectNumber());
                case Name -> {
                    var res = parser.collectUntil(() -> {
                        var item = new JsonPrimitive(parser.expectName());
                        if (parser.peek().type() == DeclTokenType.Comma) {
                            parser.expect(DeclTokenType.Comma);
                        }
                        return item;
                    }, DeclTokenType.NewLine);
                    if (res.size() == 1) {
                        yield res.get(0);
                    }
                    yield res;
                }
                case OpenParen -> parseFloatArray(parser);
                case OpenBrace -> parseObject(parser);
                default -> throw new UnsupportedOperationException(key);
            };

            obj.add(key, value);
        }, DeclTokenType.CloseBrace);
        return obj;
    }
}
