package be.twofold.valen.core.texture.pipeline;

import wtf.reversed.toolbox.math.*;

final class ReconstructZ implements Stage {
    static final ReconstructZ INSTANCE = new ReconstructZ();

    private ReconstructZ() {
    }

    @Override
    public void process(float[] tile, int pixelCount) {
        for (int i = 0; i < pixelCount * 4; i += 4) {
            float x = Math.fma(tile[i/**/], 2.0f, -1.0f);
            float y = Math.fma(tile[i + 1], 2.0f, -1.0f);
            float z = FloatMath.sqrt(Math.clamp(1.0f - x * x - y * y, 0.0f, 1.0f));
            tile[i + 2] = Math.fma(z, 0.5f, 0.5f);
        }
    }
}
