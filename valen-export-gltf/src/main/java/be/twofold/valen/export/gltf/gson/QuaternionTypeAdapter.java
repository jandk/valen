package be.twofold.valen.export.gltf.gson;

import be.twofold.valen.core.math.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class QuaternionTypeAdapter extends TypeAdapter<Quaternion> {
    @Override
    public void write(JsonWriter out, Quaternion value) throws IOException {
        out.beginArray();
        out.value(value.x());
        out.value(value.y());
        out.value(value.z());
        out.value(value.w());
        out.endArray();
    }

    @Override
    public Quaternion read(JsonReader in) throws JsonIOException {
        throw new UnsupportedOperationException();
    }
}
