package be.twofold.valen.writer.gltf.gson;

import be.twofold.valen.writer.gltf.model.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class AccessorComponentTypeTypeAdapter extends TypeAdapter<AccessorComponentType> {
    @Override
    public void write(JsonWriter out, AccessorComponentType value) throws IOException {
        out.value(value.getId());
    }

    @Override
    public AccessorComponentType read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException();
    }
}
