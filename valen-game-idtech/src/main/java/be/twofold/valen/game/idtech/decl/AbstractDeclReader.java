package be.twofold.valen.game.idtech.decl;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.idtech.decl.parser.*;
import com.google.gson.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

public abstract class AbstractDeclReader<K extends AssetID, V extends Asset, A extends Archive<K, V>> implements AssetReader<JsonObject, V> {
    private static final Pattern ItemPattern = Pattern.compile("^\\w+\\[(\\d+)]$");
    private static final CharsetDecoder Utf8Decoder = StandardCharsets.UTF_8.newDecoder();
    private static final CharsetDecoder Iso88591Decoder = StandardCharsets.ISO_8859_1.newDecoder();

    private final Map<K, JsonObject> declCache = new HashMap<>();
    private final A archive;

    public AbstractDeclReader(A archive) {
        this.archive = archive;
    }

    @Override
    public abstract boolean canRead(V asset);

    public abstract K getAssetID(String name, K baseAssetID);

    @Override
    public JsonObject read(BinaryReader reader, V asset) throws IOException {
        var buffer = reader.readBuffer(Math.toIntExact(reader.size()));
        var object = DeclParser.parse(decode(buffer));

        @SuppressWarnings("unchecked")
        var result = loadInherit(object, (K) asset.id());
        result = result.deepCopy();
        postProcessArrays(result);

        if (!result.has("edit")) {
            return new JsonObject();
        }
        return result.getAsJsonObject("edit");
    }

    private JsonObject loadInherit(JsonObject object, K baseAssetID) throws IOException {
        if (!object.has("inherit")) {
            return object;
        }

        var inheritName = object.getAsJsonPrimitive("inherit").getAsString().toLowerCase();
        K inheritID = getAssetID(inheritName, baseAssetID);

        var inherit = declCache.get(inheritID);
        if (inherit != null) {
            return merge(inherit, object);
        }

        var buffer = archive.loadAsset(inheritID, ByteBuffer.class);
        inherit = DeclParser.parse(decode(buffer));
        inherit = loadInherit(inherit, baseAssetID);
        declCache.put(inheritID, inherit);
        return merge(inherit, object);
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
            Check.argument(matcher.matches(), "Invalid id: " + entry.getKey());
            var index = Integer.parseInt(matcher.group(1));
            if (index >= size) {
                continue;
            }

            array.set(index, entry.getValue());
        }
        return array;
    }
}
