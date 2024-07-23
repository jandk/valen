package be.twofold.valen.gltf.gson;

import be.twofold.valen.gltf.model.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public final class AnimationSamplerInterpolationTypeAdapter extends TypeAdapter<AnimationSamplerInterpolation> {
    @Override
    public void write(JsonWriter out, AnimationSamplerInterpolation value) throws IOException {
        out.value(value.getValue());
    }

    @Override
    public AnimationSamplerInterpolation read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException();
    }
}
