package be.twofold.valen.game.eternal.reader.decl;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.google.gson.stream.*;

import java.io.*;
import java.util.*;

public final class NamedEnumFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<T> rawType = (Class<T>) type.getRawType();
        if (!rawType.isEnum() || !NamedEnum.class.isAssignableFrom(rawType)) {
            return null;
        }

        Class<NamedEnum> declEnumType = (Class<NamedEnum>) rawType;
        final Map<String, NamedEnum> nameToConstant = new HashMap<>();
        for (NamedEnum constant : declEnumType.getEnumConstants()) {
            nameToConstant.put(constant.getName(), constant);
        }

        return new TypeAdapter<T>() {
            public T read(JsonReader reader) throws IOException {
                return (T) nameToConstant.get(reader.nextString());
            }

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                out.value(((NamedEnum) value).getName());
            }
        }.nullSafe();
    }
}
