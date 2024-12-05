package be.twofold.valen.gltf.gson;

import be.twofold.valen.gltf.model.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class ValueEnumTypeAdapter extends TypeAdapter<ValueEnum<?>> {
    @Override
    public void write(JsonWriter out, ValueEnum<?> value) throws IOException {
        Object o = value.value();
        switch (o) {
            case String s -> out.value(s);
            case Number n -> out.value(n);
            default -> throw new UnsupportedOperationException("Unsupported type: " + o.getClass());
        }
    }

    @Override
    public ValueEnum<?> read(JsonReader in) {
        throw new UnsupportedOperationException();
    }
}
