package be.twofold.valen.gltf.types;

import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public record Vec2(
    float x,
    float y
) {
    public static final class Adapter extends TypeAdapter<Vec2> {
        @Override
        public void write(JsonWriter out, Vec2 value) throws IOException {
            out.beginArray();
            out.value(value.x());
            out.value(value.y());
            out.endArray();
        }

        @Override
        public Vec2 read(JsonReader in) throws IOException {
            in.beginArray();
            float x = (float) in.nextDouble();
            float y = (float) in.nextDouble();
            in.endArray();

            return new Vec2(x, y);
        }
    }
}
