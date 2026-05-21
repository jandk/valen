package be.twofold.valen.core.texture.shader.operation;

record ScaleAndBias(
    float scale,
    float bias
) implements OperationInPlace {
    @Override
    public void process(float[] buf, int pixelCount) {
        for (int i = 0; i < pixelCount * 4; i += 4) {
            buf[i/**/] = Math.fma(buf[i/**/], scale, bias);
            buf[i + 1] = Math.fma(buf[i + 1], scale, bias);
            buf[i + 2] = Math.fma(buf[i + 2], scale, bias);
        }
    }
}
