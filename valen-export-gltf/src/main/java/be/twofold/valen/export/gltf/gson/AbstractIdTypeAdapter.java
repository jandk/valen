package be.twofold.valen.export.gltf.gson;

import be.twofold.valen.export.gltf.model.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class AbstractIdTypeAdapter extends TypeAdapter<AbstractId> {
    @Override
    public void write(JsonWriter out, AbstractId value) throws IOException {
        out.value(value.getId());
    }

    @Override
    public AbstractId read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException();
    }
}
