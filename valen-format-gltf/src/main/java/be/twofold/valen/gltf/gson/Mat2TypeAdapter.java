package be.twofold.valen.gltf.gson;

import be.twofold.valen.gltf.types.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

final class Mat2TypeAdapter extends TypeAdapter<Mat2> {
    @Override
    public void write(JsonWriter out, Mat2 value) throws IOException {
        out.beginArray();
        out.value(value.m00());
        out.value(value.m01());
        out.value(value.m10());
        out.value(value.m11());
        out.endArray();
    }

    @Override
    public Mat2 read(JsonReader in) throws IOException {
        in.beginArray();
        float m00 = (float) in.nextDouble();
        float m01 = (float) in.nextDouble();
        float m10 = (float) in.nextDouble();
        float m11 = (float) in.nextDouble();
        in.endArray();

        return new Mat2(
            m00, m01,
            m10, m11
        );
    }
}
