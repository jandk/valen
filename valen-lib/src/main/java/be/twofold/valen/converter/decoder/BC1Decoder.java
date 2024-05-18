package be.twofold.valen.converter.decoder;

public final class BC1Decoder extends BCDecoder {
    public BC1Decoder() {
        super(8, 4);
    }

    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        BcUtils.colorBlock(src, srcPos, dst, dstPos, bpr, false);
    }
}
