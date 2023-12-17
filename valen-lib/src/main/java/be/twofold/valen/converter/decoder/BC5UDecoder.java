package be.twofold.valen.converter.decoder;

public final class BC5UDecoder extends BCDecoder {
    private final BC4UDecoder rDecoder;
    private final BC4UDecoder gDecoder;
    private final BCDecoder normalDecoder;

    /**
     * Create a new BC1 decoder.
     *
     * @param bpp                The number of bytes per pixel.
     * @param rOff               The offset of the red component.
     * @param gOff               The offset of the green component.
     * @param normalizeNormalMap Whether to add a blue component to the normal map.
     */
    public BC5UDecoder(int bpp, int rOff, int gOff, int bOff, boolean normalizeNormalMap) {
        super(16, bpp);
        this.rDecoder = new BC4UDecoder(bpp, rOff);
        this.gDecoder = new BC4UDecoder(bpp, gOff);
        this.normalDecoder = normalizeNormalMap
            ? new NormalDecoder(16, bpp, rOff, gOff, bOff)
            : new EmptyDecoder(16, bpp);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        rDecoder.decodeBlock(src, srcPos + 0, dst, dstPos, bpr);
        gDecoder.decodeBlock(src, srcPos + 8, dst, dstPos, bpr);
        normalDecoder.decodeBlock(src, srcPos + 0, dst, dstPos, bpr);
    }
}
