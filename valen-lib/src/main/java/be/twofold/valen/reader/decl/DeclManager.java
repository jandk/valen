package be.twofold.valen.reader.decl;

import be.twofold.valen.reader.decl.entitydef.EntityDefParser;
import be.twofold.valen.reader.decl.md6def.*;
import be.twofold.valen.reader.decl.parser.*;
import be.twofold.valen.resource.*;
import com.google.gson.*;

import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

public final class DeclManager {
    private static final String RootPrefix = "generated/decls/";
    private static final Pattern ItemPattern = Pattern.compile("^\\w+\\[(\\d+)]$");
    private static final CharsetDecoder Utf8Decoder = StandardCharsets.UTF_8.newDecoder();
    private static final CharsetDecoder Iso88591Decoder = StandardCharsets.ISO_8859_1.newDecoder();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new NamedEnumFactory())
            .setPrettyPrinting()
            .create();

    private static final Set<String> Unsupported = Set.of(
            "animweb",
            "articulatedfigure",
            "breakable",
            "entitydef", // Custom content per entity
            "md6def",
            "renderlayerdefinition",
            "renderparm", // Also filenames
            "renderprogflag"
    );

    private final Map<String, JsonObject> declCache = new HashMap<>();
    private final ResourceManager manager;
    private final EntityDefParser entityDefParser = new EntityDefParser();
    private final MD6DefParser md6DefParser = new MD6DefParser();

    public DeclManager(ResourceManager manager) {
        this.manager = manager;
    }

    public JsonObject load(String name) {
        var basePath = name
                .substring(RootPrefix.length())
                .substring(0, name.indexOf('/'));
        return load(basePath, name);

    }

    private JsonObject load(String basePath, String name) {
        System.out.println(basePath+"  "+ name);
        var value =  switch (basePath) {
            case "animweb", "articulatedfigure", "breakable", "renderprogflag", "renderparm", "renderlayerdefinition" ->
                    throw new UnsupportedOperationException("Unsupported decl type: " + basePath);
            case "entitydef" -> parseEntityDefDecl(name);
            default -> postProcessArrays(parseStandardDecl(name));
        };

        return postProcessDecl(basePath, value);
    }

    private JsonObject postProcessDecl(String basePath, JsonObject value) {
        JsonObject parent;
        if (value.has("inherit")) {
            var inherit = value.getAsJsonPrimitive("inherit").getAsString();
            parent = declCache.computeIfAbsent(
                RootPrefix+basePath + "/" + inherit + ".decl",
                key -> load(basePath, key)
            );
        } else {
            parent = new JsonObject();
        }

        return merge(parent, value);
    }

    private JsonObject parseStandardDecl(String name) {
        return DeclParser.parse(loadSource(name));
    }

    private JsonObject parseEntityDefDecl(String name) {
        return entityDefParser.parse(loadSource(name));
    }

    private String loadSource(String name) {
        return decode(manager.read(manager.getEntry(name, ResourceType.CompFile)));
    }


    public JsonObject merge(JsonObject parent, JsonObject child) {
        for (var childEntry : child.entrySet()) {
            var key = childEntry.getKey();
            var childValue = childEntry.getValue();
            var parentValue = parent.get(key);
            if (parentValue != null && parentValue.isJsonObject() && childValue.isJsonObject()) {
                childValue = merge(parentValue.getAsJsonObject(), childValue.getAsJsonObject());
            }
            parent.add(key, childValue);
        }
        return parent;
    }


    private String decode(byte[] bytes) {
        // Either UTF-8 or ISO-8859-1, so out is always smaller
        var in = ByteBuffer.wrap(bytes);
        try {
            return Utf8Decoder.decode(in).toString();
        } catch (CharacterCodingException e) {
            try {
                in.rewind();
                return Iso88591Decoder.decode(in).toString();
            } catch (CharacterCodingException ex) {
                throw new RuntimeException("Failed to decode", ex);
            }
        }
    }

    private JsonObject postProcessArrays(JsonObject value) {
        for (var entry : value.entrySet()) {
            if (!entry.getValue().isJsonObject()) {
                continue;
            }
            var object = entry.getValue().getAsJsonObject();
            if (object.has("num")) {
                var array = toArray(object);
                value.add(entry.getKey(), array);
            } else {
                postProcessArrays(object);
            }
        }
        return value;
    }

    private JsonArray toArray(JsonObject object) {
        var size = object.get("num").getAsInt();
        var array = new JsonArray(size);
        for (var i = 0; i < size; i++) {
            array.add(JsonNull.INSTANCE);
        }
        for (var entry : object.entrySet()) {
            if (entry.getKey().equals("num")) {
                continue;
            }
            var matcher = ItemPattern.matcher(entry.getKey());
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Invalid key: " + entry.getKey());
            }
            var index = Integer.parseInt(matcher.group(1));
            array.set(index, entry.getValue());
        }
        return array;
    }
}
