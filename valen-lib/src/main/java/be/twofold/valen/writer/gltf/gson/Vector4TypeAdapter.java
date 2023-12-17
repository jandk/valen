package be.twofold.valen.writer.gltf.gson;

import be.twofold.valen.core.math.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class Vector4TypeAdapter extends TypeAdapter<Vector4> {
    @Override
    public void write(JsonWriter out, Vector4 value) throws IOException {
        out.beginArray();
        out.value(value.x());
        out.value(value.y());
        out.value(value.z());
        out.value(value.w());
        out.endArray();
    }

    @Override
    public Vector4 read(JsonReader in) throws JsonIOException {
        throw new UnsupportedOperationException();
    }
}
