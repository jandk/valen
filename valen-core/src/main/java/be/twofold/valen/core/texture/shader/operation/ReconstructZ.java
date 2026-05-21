package be.twofold.valen.core.texture.shader.operation;

import wtf.reversed.toolbox.math.*;

record ReconstructZ() implements OperationInPlace {
    @Override
    public void process(float[] buf, int pixelCount) {
        for (int i = 0; i < pixelCount * 4; i += 4) {
            float x = Math.fma(buf[i/**/], 2.0f, -1.0f);
            float y = Math.fma(buf[i + 1], 2.0f, -1.0f);
            float z = FloatMath.sqrt(Math.clamp(1.0f - x * x - y * y, 0.0f, 1.0f));
            buf[i + 2] = Math.fma(z, 0.5f, 0.5f);
        }
    }
}
