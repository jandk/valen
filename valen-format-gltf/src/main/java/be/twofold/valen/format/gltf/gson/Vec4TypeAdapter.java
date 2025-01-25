package be.twofold.valen.format.gltf.gson;

import be.twofold.valen.format.gltf.types.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

final class Vec4TypeAdapter extends TypeAdapter<Vec4> {
    @Override
    public void write(JsonWriter out, Vec4 value) throws IOException {
        out.beginArray();
        out.value(value.x());
        out.value(value.y());
        out.value(value.z());
        out.value(value.w());
        out.endArray();
    }

    @Override
    public Vec4 read(JsonReader in) throws IOException {
        in.beginArray();
        float x = (float) in.nextDouble();
        float y = (float) in.nextDouble();
        float z = (float) in.nextDouble();
        float w = (float) in.nextDouble();
        in.endArray();

        return new Vec4(x, y, z, w);
    }
}
