package be.twofold.valen.export.gltf.gson;

import be.twofold.valen.export.gltf.model.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class AccessorTypeTypeAdapter extends TypeAdapter<AccessorType> {
    @Override
    public void write(JsonWriter out, AccessorType value) throws IOException {
        out.value(value.getValue());
    }

    @Override
    public AccessorType read(JsonReader in) {
        throw new UnsupportedOperationException();
    }
}
