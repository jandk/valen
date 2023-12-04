package be.twofold.valen.writer.gltf.gson;

import be.twofold.valen.writer.gltf.model.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class BufferViewTargetTypeAdapter extends TypeAdapter<BufferViewTarget> {
    @Override
    public void write(JsonWriter out, BufferViewTarget value) throws IOException {
        out.value(value.getId());
    }

    @Override
    public BufferViewTarget read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException();
    }
}
