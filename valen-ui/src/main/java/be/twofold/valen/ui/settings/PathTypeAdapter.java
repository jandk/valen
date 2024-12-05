package be.twofold.valen.ui.settings;

import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;
import java.nio.file.*;

final class PathTypeAdapter extends TypeAdapter<Path> {
    @Override
    public void write(JsonWriter out, Path value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public Path read(JsonReader in) throws IOException {
        return Path.of(in.nextString());
    }
}
