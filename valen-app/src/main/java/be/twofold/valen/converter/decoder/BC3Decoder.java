package be.twofold.valen.converter.decoder;

public final class BC3Decoder extends BCDecoder {
    private final BC1Decoder rgbDecoder;
    private final BC4UDecoder aDecoder;

    /**
     * Create a new BC1 decoder.
     *
     * @param bpp  The number of bytes per pixel.
     * @param rOff The offset of the red component.
     * @param gOff The offset of the green component.
     */
    public BC3Decoder(int bpp, int rOff, int gOff, int bOff, int aOff) {
        super(16, bpp);
        this.rgbDecoder = new BC1Decoder(bpp, rOff, gOff, bOff);
        this.aDecoder = new BC4UDecoder(bpp, aOff);
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        aDecoder.decodeBlock(src, srcPos + 0, dst, dstPos, bpr);
        rgbDecoder.decodeBlock(src, srcPos + 8, dst, dstPos, bpr);
    }
}
