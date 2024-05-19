package be.twofold.valen.core.texture.decoder;

public final class BC4UDecoder extends BCDecoder {
    public BC4UDecoder() {
        super(8, 1);
    }

    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        BcUtils.alphaBlock(src, srcPos, dst, dstPos, bpr, bpp);
    }
}
