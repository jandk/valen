package be.twofold.valen.gltf.gson;

import be.twofold.valen.gltf.model.image.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class MimeTypeTypeAdapter extends TypeAdapter<ImageMimeType> {
    @Override
    public void write(JsonWriter out, ImageMimeType value) throws IOException {
        out.value(value.getValue());
    }

    @Override
    public ImageMimeType read(JsonReader in) {
        throw new UnsupportedOperationException();
    }
}
