package be.twofold.valen.format.gltf.gson;

import be.twofold.valen.format.gltf.model.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

final class SerializableEnumTypeAdapter extends TypeAdapter<SerializableEnum<?>> {
    @Override
    public void write(JsonWriter out, SerializableEnum<?> value) throws IOException {
        Object o = value.value();
        switch (o) {
            case String s -> out.value(s);
            case Number n -> out.value(n);
            default -> throw new UnsupportedOperationException("Unsupported type: " + o.getClass());
        }
    }

    @Override
    public SerializableEnum<?> read(JsonReader in) {
        throw new UnsupportedOperationException();
    }
}
