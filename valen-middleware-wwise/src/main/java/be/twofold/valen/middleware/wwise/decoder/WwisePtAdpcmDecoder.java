package be.twofold.valen.middleware.wwise.decoder;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.audio.decoder.*;
import wtf.reversed.toolbox.collect.*;

public final class WwisePtAdpcmDecoder extends AdpcmDecoder {
    private static final byte[] STEP = {+1, 3, 5, 7, 10, 14, 20, 28};
    private static final byte[] NEXT = {-1, 0, 0, 0, +1, +1, +2, +2};

    private static final int MAX_INDEX = 11;

    @Override
    public AudioCodec source() {
        return AudioCodec.WWISE_PTADPCM;
    }

    @Override
    protected void decodeChannel(Bytes src, Bytes.Mutable dst, int dstOff, int stride) {
        int hist2 = src.getShort(0);
        int hist1 = src.getShort(2);
        int index = Math.min(src.getUnsigned(4), MAX_INDEX);
        dst.setShort(dstOff, (short) hist2);
        dst.setShort(dstOff + stride, (short) hist1);

        for (int i = 0, o = dstOff + 2 * stride; i < 62; i++, o += stride) {
            int b = src.getUnsigned(5 + (i >> 1));
            int n = (i & 1) == 0 ? b & 0x0F : b >> 4;

            int m = n < 8 ? 7 - n : n - 8;
            int step = (STEP[m] << index) >> 1;
            step = n < 8 ? -step : step;
            int sample = Math.clamp(step + 2 * hist1 - hist2, Short.MIN_VALUE, Short.MAX_VALUE);
            index = Math.clamp(index + NEXT[m], 0, MAX_INDEX);
            dst.setShort(o, (short) sample);
            hist2 = hist1;
            hist1 = sample;
        }
    }

    @Override
    protected int blockSize(Audio audio) {
        return 36 * audio.channels().size();
    }

    @Override
    protected int samplesPerBlock(Audio audio) {
        return 64;
    }
}
