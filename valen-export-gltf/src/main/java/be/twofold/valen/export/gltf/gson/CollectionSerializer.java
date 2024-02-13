package be.twofold.valen.export.gltf.gson;

import com.google.gson.*;

import java.lang.reflect.*;
import java.util.*;

public final class CollectionSerializer implements JsonSerializer<Collection<?>> {
    @Override
    public JsonElement serialize(Collection<?> src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null || src.isEmpty()) {
            return JsonNull.INSTANCE;
        }

        JsonArray array = new JsonArray();
        for (Object element : src) {
            array.add(context.serialize(element));
        }
        return array;
    }
}
