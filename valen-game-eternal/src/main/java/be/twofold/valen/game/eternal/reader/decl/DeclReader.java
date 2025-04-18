package be.twofold.valen.game.eternal.reader.decl;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.reader.decl.parser.*;
import be.twofold.valen.game.eternal.resource.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

public final class DeclReader implements AssetReader<JsonObject, EternalAsset> {
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
    private final EternalArchive archive;

    public DeclReader(EternalArchive archive) {
        this.archive = archive;
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        if (resource.id().type() != ResourceType.RsStreamFile) {
            return false;
        }

        var name = resource.id().name().name();
        if (!name.startsWith(RootPrefix)) {
            return false;
        }

        var basePath = getBasePath(name);
        return !Unsupported.contains(basePath);
    }

    @Override
    public JsonObject read(DataSource source, EternalAsset resource) throws IOException {
        var buffer = source.readBuffer(Math.toIntExact(source.size()));
        var object = DeclParser.parse(decode(buffer));

        var result = loadInherit(object, resource.id().fullName());
        result = result.deepCopy();
        postProcessArrays(result);

        if (!result.has("edit")) {
            return new JsonObject();
        }
        return result.getAsJsonObject("edit");
    }

    private JsonObject loadInherit(JsonObject object, String name) throws IOException {
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
        var resourceKey = EternalAssetID.from(fullName, ResourceType.RsStreamFile);
        if (!archive.exists(resourceKey)) {
            return object;
        }

        var buffer = archive.loadAsset(resourceKey, ByteBuffer.class);
        parent = DeclParser.parse(decode(buffer));
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


    private String decode(ByteBuffer buffer) throws IOException {
        // Either UTF-8 or ISO-8859-1, so out is always smaller
        try {
            return Utf8Decoder.decode(buffer).toString();
        } catch (CharacterCodingException e) {
            try {
                buffer.rewind();
                return Iso88591Decoder.decode(buffer).toString();
            } catch (CharacterCodingException ex) {
                throw new IOException("Failed to decode", ex);
            }
        }
    }


    private JsonObject merge(JsonObject parent, JsonObject child) {
        var result = parent.deepCopy();
        for (var entry : child.entrySet()) {
            var key = entry.getKey();
            var value = entry.getValue();

            var oldEntry = result.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(key))
                .findFirst().orElse(null);
            // var oldValue = result.get(key);
            if (oldEntry != null && oldEntry.getValue().isJsonObject() && value.isJsonObject()) {
                value = merge(oldEntry.getValue().getAsJsonObject(), value.getAsJsonObject());
                result.add(entry.getKey(), value);
            } else {
                result.add(key, value);
            }
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
                throw new IllegalArgumentException("Invalid id: " + entry.getKey());
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
