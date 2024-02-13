package be.twofold.valen.export.gltf.gson;

import be.twofold.valen.core.math.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class Matrix4TypeAdapter extends TypeAdapter<Matrix4> {
    @Override
    public void write(JsonWriter out, Matrix4 value) throws IOException {
        out.beginArray();
        out.value(value.m00());
        out.value(value.m01());
        out.value(value.m02());
        out.value(value.m03());
        out.value(value.m10());
        out.value(value.m11());
        out.value(value.m12());
        out.value(value.m13());
        out.value(value.m20());
        out.value(value.m21());
        out.value(value.m22());
        out.value(value.m23());
        out.value(value.m30());
        out.value(value.m31());
        out.value(value.m32());
        out.value(value.m33());
        out.endArray();
    }

    @Override
    public Matrix4 read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException();
    }
}
