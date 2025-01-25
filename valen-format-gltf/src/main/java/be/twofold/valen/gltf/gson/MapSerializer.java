package be.twofold.valen.gltf.gson;

import com.google.gson.*;

import java.lang.reflect.*;
import java.util.*;

final class MapSerializer implements JsonSerializer<Map<?, ?>> {
    @Override
    public JsonElement serialize(Map<?, ?> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || src.isEmpty()) {
            return JsonNull.INSTANCE;
        }

        var object = new JsonObject();
        for (var entry : src.entrySet()) {
            object.add(entry.getKey().toString(), context.serialize(entry.getValue()));
        }
        return object;
    }
}
