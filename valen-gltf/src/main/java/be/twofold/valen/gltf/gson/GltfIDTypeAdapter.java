package be.twofold.valen.gltf.gson;

import be.twofold.valen.gltf.model.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class GltfIDTypeAdapter extends TypeAdapter<GltfID> {
    @Override
    public void write(JsonWriter out, GltfID value) throws IOException {
        out.value(value.id());
    }

    @Override
    public GltfID read(JsonReader in) {
        throw new UnsupportedOperationException();
    }
}
