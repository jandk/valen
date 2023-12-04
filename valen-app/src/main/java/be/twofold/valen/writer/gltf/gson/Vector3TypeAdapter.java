package be.twofold.valen.writer.gltf.gson;

import be.twofold.valen.core.math.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class Vector3TypeAdapter extends TypeAdapter<Vector3> {
    @Override
    public void write(JsonWriter out, Vector3 value) throws IOException {
        out.beginArray();
        out.value(value.x());
        out.value(value.y());
        out.value(value.z());
        out.endArray();
    }

    @Override
    public Vector3 read(JsonReader in) throws JsonIOException {
        throw new UnsupportedOperationException();
    }
}
