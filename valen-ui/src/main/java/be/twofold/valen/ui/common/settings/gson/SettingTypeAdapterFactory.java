package be.twofold.valen.ui.common.settings.gson;

import be.twofold.valen.core.util.*;
import be.twofold.valen.ui.common.settings.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import com.google.gson.stream.*;

import java.io.*;
import java.lang.reflect.*;

public final class SettingTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        var rawType = type.getRawType();
        if (!Setting.class.isAssignableFrom(rawType)) {
            return null;
        }

        var elementType = ((ParameterizedType) type.getType()).getActualTypeArguments()[0];
        var elementTypeAdapter = gson.getAdapter(TypeToken.get(elementType));

        @SuppressWarnings("rawtypes")
        var result = new Adapter(elementTypeAdapter);
        return result;
    }

    private static final class Adapter<E> extends TypeAdapter<Setting<E>> {
        private final TypeAdapter<E> elementTypeAdapter;

        private Adapter(TypeAdapter<E> elementTypeAdapter) {
            this.elementTypeAdapter = Check.notNull(elementTypeAdapter, "elementTypeAdapter");
        }

        @Override
        public void write(JsonWriter out, Setting<E> value) throws IOException {
            if (value.get().isEmpty()) {
                out.nullValue();
                return;
            }
            elementTypeAdapter.write(out, value.get().get());
        }

        @Override
        public Setting<E> read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return new Setting<>();
            }
            return new Setting<>(elementTypeAdapter.read(in));
        }
    }
}
