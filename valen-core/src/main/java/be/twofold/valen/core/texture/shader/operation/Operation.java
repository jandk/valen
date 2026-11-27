package be.twofold.valen.core.texture.shader.operation;

import be.twofold.valen.core.texture.*;

import java.util.*;

@FunctionalInterface
public interface Operation {
    void process(float[] src, float[] dst, int pixelCount);

    static Operation fill(float value, Channel... channels) {
        return new Fill(value, Set.of(channels));
    }

    static Operation invert(Channel... channels) {
        return new Invert(Set.of(channels));
    }

    static Operation reconstructZ() {
        return new ReconstructZ();
    }

    static Operation scaleAndBias(float scale, float bias) {
        return new ScaleAndBias(scale, bias);
    }

    static Operation splat(Channel source, Channel... targets) {
        return new Splat(source, Set.of(targets));
    }
}
