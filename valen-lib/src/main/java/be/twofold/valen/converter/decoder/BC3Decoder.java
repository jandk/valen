package be.twofold.valen.converter.decoder;

public final class BC3Decoder extends BCDecoder {
    private final BC1Decoder rgbDecoder;
    private final BC4UDecoder aDecoder;

    /**
     * Create a new BC1 decoder.
     */
    public BC3Decoder() {
        super(16, 4);
        this.rgbDecoder = new BC1Decoder(true);
        this.aDecoder = new BC4UDecoder(4, 3);
    }

    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        rgbDecoder.decodeBlock(src, srcPos + 8, dst, dstPos, bpr);
        aDecoder.decodeBlock(src, srcPos, dst, dstPos, bpr);
    }
}
