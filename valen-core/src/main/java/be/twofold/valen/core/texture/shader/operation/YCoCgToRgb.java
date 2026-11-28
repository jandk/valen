package be.twofold.valen.core.texture.shader.operation;

record YCoCgToRgb(
    float chromaBias,
    boolean scaled
) implements OperationInPlace {
    @Override
    public void process(float[] buf, int pixelCount) {
        for (int i = 0; i < pixelCount * 4; i += 4) {
            float scale = scaled
                ? 1.0f / Math.fma(buf[i + 3], 255.0f / 8.0f, 1.0f)
                : 1.0f;
            float bias = -chromaBias * scale;

            float y = buf[i/**/];
            float co = Math.fma(buf[i + 1], scale, bias);
            float cg = Math.fma(buf[i + 2], scale, bias);

            buf[i/**/] = y + co - cg;
            buf[i + 1] = y + cg;
            buf[i + 2] = y - co - cg;
            if (scaled) {
                buf[i + 3] = 1.0f;
            }
        }
    }
}
