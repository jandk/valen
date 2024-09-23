package be.twofold.valen.gltf.gson;

import be.twofold.valen.gltf.model.accessor.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class AccessorComponentTypeTypeAdapter extends TypeAdapter<AccessorComponentType> {
    @Override
    public void write(JsonWriter out, AccessorComponentType value) throws IOException {
        out.value(value.id());
    }

    @Override
    public AccessorComponentType read(JsonReader in) {
        throw new UnsupportedOperationException();
    }
}
