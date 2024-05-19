package be.twofold.valen.converter.decoder;

public final class BC5UDecoder extends BCDecoder {
    private final BCDecoder normalDecoder;

    /**
     * Create a new BC1 decoder.
     */
    public BC5UDecoder(boolean normalizeNormalMap) {
        super(16, 3);
        this.normalDecoder = normalizeNormalMap
            ? new NormalDecoder(16, 3)
            : new EmptyDecoder(16, 3);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        BcUtils.alphaBlock(src, srcPos + 0, dst, dstPos + 0, bpr, 3);
        BcUtils.alphaBlock(src, srcPos + 8, dst, dstPos + 1, bpr, 3);
        normalDecoder.decodeBlock(src, srcPos + 0, dst, dstPos, bpr);
    }
}
