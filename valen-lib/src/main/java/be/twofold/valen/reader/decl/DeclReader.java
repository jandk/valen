package be.twofold.valen.reader.decl;

import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.decl.entitydef.EntityDefParser;
import be.twofold.valen.reader.decl.parser.*;
import be.twofold.valen.resource.*;
import com.google.gson.*;
import jakarta.inject.*;

import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

@Singleton
public final class DeclReader implements ResourceReader<JsonObject> {
    private static final String RootPrefix = "generated/decls/";
    private static final Pattern ItemPattern = Pattern.compile("^\\w+\\[(\\d+)]$");
    private static final CharsetDecoder Utf8Decoder = StandardCharsets.UTF_8.newDecoder();
    private static final CharsetDecoder Iso88591Decoder = StandardCharsets.ISO_8859_1.newDecoder();

    private static final EntityDefParser entityDefParser = new EntityDefParser();

    private static final Set<String> Unsupported = Set.of(
        "animweb",
        "articulatedfigure",
        "breakable",
        //"entitydef", // Custom content per entity
        "md6def",
        "renderlayerdefinition",
        "renderparm", // Also filenames
        "renderprogflag"
    );

    private final Map<String, JsonObject> declCache = new HashMap<>();
    private final ResourceManager resourceManager;

    @Inject
    public DeclReader(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    @Override
    public boolean canRead(Resource entry) {
        if (entry.type() != ResourceType.RsStreamFile) {
            return false;
        }

        var name = entry.name().name().substring(RootPrefix.length());
        var basePath = name.substring(0, name.indexOf('/'));
        return !Unsupported.contains(basePath);
    }

    @Override
    public JsonObject read(BetterBuffer buffer, Resource resource) {
        byte[] bytes = buffer.getBytes(buffer.length());
        return DeclParser.parse(decode(bytes));
    }

    public JsonObject load(String name) {
        var basePath = name
            .substring(0, name.indexOf('/'));

        if (Unsupported.contains(basePath)) {
            throw new UnsupportedOperationException("Unsupported decl type: " + basePath);
        }

        JsonObject object = load(basePath, name);
        postProcessArrays(object);
        return object;
    }

    private JsonObject load(String basePath, String name) {
        System.out.println("Loading decl: " + name);
        var value = getJsonObject(name, basePath);

        JsonObject parent;
        if (value.has("inherit")) {
            var inherit = value.getAsJsonPrimitive("inherit").getAsString();
            var key = basePath + "/" + inherit + ".decl";
            parent = declCache.get(key);
            if (parent == null) {
                parent = load(basePath, key);
                declCache.put(key, parent);
            }
        } else {
            parent = new JsonObject();
        }

        return merge(parent, value);
    }

    private JsonObject getJsonObject(String name, String basePath) {
        var resource = resourceManager.get(RootPrefix + name, ResourceType.RsStreamFile);
        byte[] bytes = resourceManager.read(resource);
        return switch (basePath) {
            case "entitydef" -> entityDefParser.parse(decode(bytes));
            default -> DeclParser.parse(decode(bytes));
        };
    }

    public JsonObject merge(JsonObject parent, JsonObject child) {
        JsonObject result = parent.deepCopy();
        for (var entry : child.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();

            var oldValue = result.get(key);
            if (oldValue != null && oldValue.isJsonObject() && value.isJsonObject()) {
                value = merge(oldValue.getAsJsonObject(), value.getAsJsonObject());
            }
            result.add(key, value);
        }
        return result;
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

    public static void postProcessArrays(JsonObject value) {
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
    }

    private static JsonArray toArray(JsonObject object) {
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
