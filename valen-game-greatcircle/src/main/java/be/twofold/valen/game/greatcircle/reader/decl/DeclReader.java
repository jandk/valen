package be.twofold.valen.game.greatcircle.reader.decl;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.*;
import be.twofold.valen.game.greatcircle.resource.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

public final class DeclReader implements ResourceReader<JsonObject> {
    private static final String RootPrefix = "";
    private static final Pattern ItemPattern = Pattern.compile("^\\w+\\[(\\d+)]$");
    private static final CharsetDecoder Utf8Decoder = StandardCharsets.UTF_8.newDecoder();
    private static final CharsetDecoder Iso88591Decoder = StandardCharsets.ISO_8859_1.newDecoder();

    private final Map<String, JsonObject> declCache = new HashMap<>();
    private final GreatCircleArchive archive;

    public DeclReader(GreatCircleArchive archive) {
        this.archive = archive;
    }

    @Override
    public boolean canRead(ResourceKey key) {
        return true;
    }

    @Override
    public JsonObject read(DataSource source, Asset asset) throws IOException {
        var bytes = source.readBytes(Math.toIntExact(source.size()));
        var object = DeclParser.parse(decode(bytes));

        var result = loadInherit(object);
        result = result.deepCopy();
        postProcessArrays(result);

        if (!result.has("edit")) {
            return new JsonObject();
        }
        return result.getAsJsonObject("edit");
    }

    private JsonObject loadInherit(JsonObject object) throws IOException {
        if (!object.has("inherit")) {
            return object;
        }

        var inherit = object.getAsJsonPrimitive("inherit").getAsString();

        var parent = declCache.get(inherit);
        if (parent != null) {
            return merge(parent, object);
        }

        var resourceKey = new ResourceKey(new ResourceName(inherit), ResourceType.material2, ResourceVariation.RES_VAR_NONE);
        if (!archive.exists(resourceKey)) {
            return object;
        }

        var bytes = archive.loadAsset(resourceKey, byte[].class);
        parent = DeclParser.parse(decode(bytes));
        parent = loadInherit(parent);
        declCache.put(inherit, parent);
        return merge(parent, object);
    }

    private String getBasePath(String name) {
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
