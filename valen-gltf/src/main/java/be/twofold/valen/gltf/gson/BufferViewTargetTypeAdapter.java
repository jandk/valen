package be.twofold.valen.gltf.gson;

import be.twofold.valen.gltf.model.buffer.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class BufferViewTargetTypeAdapter extends TypeAdapter<BufferViewTarget> {
    @Override
    public void write(JsonWriter out, BufferViewTarget value) throws IOException {
        out.value(value.getId());
    }

    @Override
    public BufferViewTarget read(JsonReader in) {
        throw new UnsupportedOperationException();
    }
}
