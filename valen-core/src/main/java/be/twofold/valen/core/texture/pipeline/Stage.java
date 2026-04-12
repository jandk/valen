package be.twofold.valen.core.texture.pipeline;

sealed interface Stage permits ReconstructZ, ScaleAndBias, Splat, Tonemap {
    void process(float[] tile, int pixelCount);
}
