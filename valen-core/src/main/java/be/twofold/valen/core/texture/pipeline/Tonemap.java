package be.twofold.valen.core.texture.pipeline;

final class Tonemap implements Stage {
    static final Tonemap INSTANCE = new Tonemap();

    private Tonemap() {
    }

    @Override
    public void process(float[] tile, int pixelCount) {
        for (int i = 0; i < pixelCount * 4; i += 4) {
            tile[i/**/] = Math.clamp(tile[i/**/], 0.0f, 1.0f);
            tile[i + 1] = Math.clamp(tile[i + 1], 0.0f, 1.0f);
            tile[i + 2] = Math.clamp(tile[i + 2], 0.0f, 1.0f);
        }
    }
}
