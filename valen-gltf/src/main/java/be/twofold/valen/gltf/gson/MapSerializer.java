package be.twofold.valen.gltf.gson;

import com.google.gson.*;

import java.lang.reflect.*;
import java.util.*;

public final class MapSerializer implements JsonSerializer<Map<?, ?>> {
    @Override
    public JsonElement serialize(Map<?, ?> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || src.isEmpty()) {
            return JsonNull.INSTANCE;
        }

        JsonObject obj = new JsonObject();
        src.forEach((key, value) -> {
            obj.add(key.toString(), context.serialize(value));
        });
        return obj;
    }
}
