package be.twofold.valen.writer.gltf.gson;

import be.twofold.valen.core.math.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class Vector2TypeAdapter extends TypeAdapter<Vector2> {
    @Override
    public void write(JsonWriter out, Vector2 value) throws IOException {
        out.beginArray();
        out.value(value.x());
        out.value(value.y());
        out.endArray();
    }

    @Override
    public Vector2 read(JsonReader in) throws JsonIOException {
        throw new UnsupportedOperationException();
    }
}
