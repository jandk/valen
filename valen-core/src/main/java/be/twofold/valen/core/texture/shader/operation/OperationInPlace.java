package be.twofold.valen.core.texture.shader.operation;

@FunctionalInterface
public interface OperationInPlace extends Operation {
    void process(float[] buf, int pixelCount);

    @Override
    default void process(float[] src, float[] dst, int pixelCount) {
        System.arraycopy(src, 0, dst, 0, pixelCount * 4);
        process(dst, pixelCount);
    }
}
