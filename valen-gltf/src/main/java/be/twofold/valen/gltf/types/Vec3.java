package be.twofold.valen.gltf.types;

import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public record Vec3(
    float x,
    float y,
    float z
) {
    public static final class Adapter extends TypeAdapter<Vec3> {
        @Override
        public void write(JsonWriter out, Vec3 value) throws IOException {
            out.beginArray();
            out.value(value.x());
            out.value(value.y());
            out.value(value.z());
            out.endArray();
        }

        @Override
        public Vec3 read(JsonReader in) throws IOException {
            in.beginArray();
            float x = (float) in.nextDouble();
            float y = (float) in.nextDouble();
            float z = (float) in.nextDouble();
            in.endArray();

            return new Vec3(x, y, z);
        }
    }
}
