package be.twofold.valen.format.gltf.gson;

import be.twofold.valen.format.gltf.types.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

final class Vec2TypeAdapter extends TypeAdapter<Vec2> {
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
