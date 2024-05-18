package be.twofold.valen.converter.decoder;

public final class BC2Decoder extends BCDecoder {
    public BC2Decoder() {
        super(16, 4);
    }

    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        BcUtils.colorBlock(src, srcPos + 8, dst, dstPos, bpr, true);
        BcUtils.alphaBlock16(src, srcPos, dst, dstPos + 3, bpr);
    }
}
