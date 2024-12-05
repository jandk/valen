package be.twofold.valen.gltf.gson;

import be.twofold.valen.gltf.types.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class Mat3TypeAdapter extends TypeAdapter<Mat3> {
    @Override
    public void write(JsonWriter out, Mat3 value) throws IOException {
        out.beginArray();
        out.value(value.m00());
        out.value(value.m01());
        out.value(value.m02());
        out.value(value.m10());
        out.value(value.m11());
        out.value(value.m12());
        out.value(value.m20());
        out.value(value.m21());
        out.value(value.m22());
        out.endArray();
    }

    @Override
    public Mat3 read(JsonReader in) throws IOException {
        in.beginArray();
        float m00 = (float) in.nextDouble();
        float m01 = (float) in.nextDouble();
        float m02 = (float) in.nextDouble();
        float m10 = (float) in.nextDouble();
        float m11 = (float) in.nextDouble();
        float m12 = (float) in.nextDouble();
        float m20 = (float) in.nextDouble();
        float m21 = (float) in.nextDouble();
        float m22 = (float) in.nextDouble();
        in.endArray();

        return new Mat3(
            m00, m01, m02,
            m10, m11, m12,
            m20, m21, m22
        );
    }
}
