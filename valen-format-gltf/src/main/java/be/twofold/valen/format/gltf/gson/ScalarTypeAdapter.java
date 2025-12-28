package be.twofold.valen.format.gltf.gson;

import be.twofold.valen.format.gltf.types.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

final class ScalarTypeAdapter extends TypeAdapter<Scalar> {
    @Override
    public void write(JsonWriter out, Scalar value) throws IOException {
        out.beginArray();
        out.value(value.value());
        out.endArray();
    }

    @Override
    public Scalar read(JsonReader in) throws IOException {
        in.beginArray();
        var result = new Scalar((float) in.nextDouble());
        in.endArray();
        return result;
    }
}
