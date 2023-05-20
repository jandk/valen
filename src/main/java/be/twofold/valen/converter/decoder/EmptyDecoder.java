package be.twofold.valen.converter.decoder;

public final class EmptyDecoder extends BCDecoder {
    public EmptyDecoder(int bpb, int bpp) {
        super(bpb, bpp);
    }

    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        // do nothing
    }
}
