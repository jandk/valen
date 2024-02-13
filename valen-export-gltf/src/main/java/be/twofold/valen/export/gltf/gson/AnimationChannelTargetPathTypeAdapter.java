package be.twofold.valen.export.gltf.gson;

import be.twofold.valen.export.gltf.model.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public class AnimationChannelTargetPathTypeAdapter extends TypeAdapter<AnimationChannelTargetPath> {
    @Override
    public void write(JsonWriter out, AnimationChannelTargetPath value) throws IOException {
        out.value(value.getValue());
    }

    @Override
    public AnimationChannelTargetPath read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException();
    }
}
