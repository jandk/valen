package be.twofold.valen.gltf.gson;

import com.google.gson.*;

import java.lang.reflect.*;
import java.util.*;

final class CollectionSerializer implements JsonSerializer<Collection<?>> {
    @Override
    public JsonElement serialize(Collection<?> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || src.isEmpty()) {
            return JsonNull.INSTANCE;
        }

        var array = new JsonArray();
        for (var element : src) {
            array.add(context.serialize(element));
        }
        return array;
    }
}
