package be.twofold.valen.export.gltf.gson;

import be.twofold.valen.export.gltf.model.*;
import com.google.gson.*;
import com.google.gson.stream.*;

import java.io.*;

public class AnimationSamplerInterpolationTypeAdapter extends TypeAdapter<AnimationSamplerInterpolation> {
    @Override
    public void write(JsonWriter out, AnimationSamplerInterpolation value) throws IOException {
        out.value(value.getValue());
    }

    @Override
    public AnimationSamplerInterpolation read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException();
    }
}
