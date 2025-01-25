package be.twofold.valen.gltf.gson;

import be.twofold.valen.gltf.types.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

final class ScalarTypeAdapter extends TypeAdapter<Scalar> {
    @Override
    public void write(JsonWriter out, Scalar value) throws IOException {
        out.value(value.value());
    }

    @Override
    public Scalar read(JsonReader in) throws IOException {
        return new Scalar((float) in.nextDouble());
    }
}
