package be.twofold.valen.gltf.gson;

import be.twofold.valen.gltf.types.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

final class Mat4TypeAdapter extends TypeAdapter<Mat4> {
    @Override
    public void write(JsonWriter out, Mat4 value) throws IOException {
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
    public Mat4 read(JsonReader in) throws IOException {
        in.beginArray();
        float m00 = (float) in.nextDouble();
        float m01 = (float) in.nextDouble();
        float m02 = (float) in.nextDouble();
        float m03 = (float) in.nextDouble();
        float m10 = (float) in.nextDouble();
        float m11 = (float) in.nextDouble();
        float m12 = (float) in.nextDouble();
        float m13 = (float) in.nextDouble();
        float m20 = (float) in.nextDouble();
        float m21 = (float) in.nextDouble();
        float m22 = (float) in.nextDouble();
        float m23 = (float) in.nextDouble();
        float m30 = (float) in.nextDouble();
        float m31 = (float) in.nextDouble();
        float m32 = (float) in.nextDouble();
        float m33 = (float) in.nextDouble();
        in.endArray();

        return new Mat4(
            m00, m01, m02, m03,
            m10, m11, m12, m13,
            m20, m21, m22, m23,
            m30, m31, m32, m33
        );
    }
}
