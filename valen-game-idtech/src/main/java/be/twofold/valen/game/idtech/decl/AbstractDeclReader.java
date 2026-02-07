package be.twofold.valen.game.idtech.decl;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.idtech.decl.parser.*;
import com.google.gson.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;
import java.util.regex.*;

public abstract class AbstractDeclReader<K extends AssetID, V extends Asset> implements AssetReader<JsonObject, V> {
    private static final Pattern ItemPattern = Pattern.compile("^\\w+\\[(\\d+)]$");
    private static final CharsetDecoder Utf8Decoder = StandardCharsets.UTF_8.newDecoder();
    private static final CharsetDecoder Iso88591Decoder = StandardCharsets.ISO_8859_1.newDecoder();

    private final Map<K, JsonObject> declCache = new HashMap<>();

    public abstract K getAssetID(String name, K baseAssetID);

    @Override
    public abstract boolean canRead(V asset);

    @Override
    public JsonObject read(BinarySource source, V asset, LoadingContext context) throws IOException {
        var bytes = source.readBytes(Math.toIntExact(source.size()));
        var object = DeclParser.parse(decode(bytes));

        @SuppressWarnings("unchecked")
        var result = loadInherit(object, (K) asset.id(), context);
        result = result.deepCopy();
        postProcessArrays(result);

        if (!result.has("edit")) {
            return new JsonObject();
        }
        return result.getAsJsonObject("edit");
    }

    public void clearCache() {
        declCache.clear();
    }

    private JsonObject loadInherit(JsonObject object, K baseAssetID, LoadingContext context) throws IOException {
        if (!object.has("inherit")) {
            return object;
        }

        var inheritName = object.getAsJsonPrimitive("inherit").getAsString().toLowerCase();
        K inheritID = getAssetID(inheritName, baseAssetID);

        var inherit = declCache.get(inheritID);
        if (inherit != null) {
            return merge(inherit, object);
        }

        var bytes = context.load(inheritID, Bytes.class);
        inherit = DeclParser.parse(decode(bytes));
        inherit = loadInherit(inherit, baseAssetID, context);
        declCache.put(inheritID, inherit);
        return merge(inherit, object);
    }


    private String decode(Bytes bytes) throws IOException {
        // Either UTF-8 or ISO-8859-1, so out is always smaller
        try {
            return Utf8Decoder.decode(bytes.asBuffer()).toString();
        } catch (CharacterCodingException e) {
            try {
                return Iso88591Decoder.decode(bytes.asBuffer()).toString();
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
