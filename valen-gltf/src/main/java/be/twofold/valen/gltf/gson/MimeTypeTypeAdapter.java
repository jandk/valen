package be.twofold.valen.gltf.gson;

import be.twofold.valen.gltf.model.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class MimeTypeTypeAdapter extends TypeAdapter<MimeType> {
    @Override
    public void write(JsonWriter out, MimeType value) throws IOException {
        out.value(value.getValue());
    }

    @Override
    public MimeType read(JsonReader in) {
        throw new UnsupportedOperationException();
    }
}
