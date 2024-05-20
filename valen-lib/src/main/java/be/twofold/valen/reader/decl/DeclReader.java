package be.twofold.valen.reader.decl;

import be.twofold.valen.core.io.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.decl.parser.*;
import be.twofold.valen.resource.*;
import com.google.gson.*;
import jakarta.inject.*;

import java.io.*;
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

    private static final Set<String> Unsupported = Set.of(
        "animweb",
        "articulatedfigure",
        "breakable",
        "entitydef", // Custom content per entity
        "material2", // Has a custom reader
        "md6def",
        "renderlayerdefinition",
        "renderparm", // Has a custom reader
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
        if (!entry.nameString().startsWith(RootPrefix)) {
            return false;
        }

        var basePath = getBasePath(entry.nameString());
        return !Unsupported.contains(basePath);
    }

    @Override
    public JsonObject read(DataSource source, Resource resource) throws IOException {
        var bytes = source.readBytes(Math.toIntExact(source.size()));
        var object = DeclParser.parse(decode(bytes));

        var result = loadInherit(object, resource.nameString());
        result = result.deepCopy();
        postProcessArrays(result);

        if (!result.has("edit")) {
            return new JsonObject();
        }
        return result.getAsJsonObject("edit");
    }

    private JsonObject loadInherit(JsonObject object, String name) {
        if (!object.has("inherit")) {
            return object;
        }

        var inherit = object.getAsJsonPrimitive("inherit").getAsString();
        var basePath = getBasePath(name);
        var key = basePath + "/" + inherit;

        var parent = declCache.get(key);
        if (parent != null) {
            return merge(parent, object);
        }

        var fullName = RootPrefix + key + ".decl";
        var resource = resourceManager.get(fullName, ResourceType.RsStreamFile);
        if (resource == null) {
            System.err.println("Missing decl: " + fullName);
            return object;
        }

        var bytes = resourceManager.read(resource);
        parent = DeclParser.parse(decode(bytes));
        parent = loadInherit(parent, fullName);
        declCache.put(key, parent);
        return merge(parent, object);
    }

    private String getBasePath(String name) {
        if (!name.startsWith(RootPrefix)) {
            throw new IllegalArgumentException("Invalid decl name: " + name);
        }

        name = name.substring(RootPrefix.length());
        return name.substring(0, name.indexOf('/'));
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


    private JsonObject merge(JsonObject parent, JsonObject child) {
        var result = parent.deepCopy();
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

    private void postProcessArrays(JsonObject value) {
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
            if (index >= size) {
                continue;
            }

            array.set(index, entry.getValue());
        }
        return array;
    }
}
