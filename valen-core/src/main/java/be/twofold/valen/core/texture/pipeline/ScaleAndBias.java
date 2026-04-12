package be.twofold.valen.core.texture.pipeline;

record ScaleAndBias(float scale, float bias) implements Stage {
    @Override
    public void process(float[] tile, int pixelCount) {
        for (int i = 0; i < pixelCount * 4; i += 4) {
            tile[i/**/] = Math.fma(tile[i/**/], scale, bias);
            tile[i + 1] = Math.fma(tile[i + 1], scale, bias);
            tile[i + 2] = Math.fma(tile[i + 2], scale, bias);
        }
    }
}
