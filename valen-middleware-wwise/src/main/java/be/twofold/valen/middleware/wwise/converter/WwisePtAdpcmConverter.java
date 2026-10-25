package be.twofold.valen.middleware.wwise.converter;

import be.twofold.valen.core.audio.*;
import wtf.reversed.toolbox.collect.*;

public final class WwisePtAdpcmConverter implements AudioConverter {
    private static final byte[] STEP = {+1, 3, 5, 7, 10, 14, 20, 28};
    private static final byte[] NEXT = {-1, 0, 0, 0, +1, +1, +2, +2};

    private static final int BLOCK = 36;
    private static final int SAMPLES = 64;
    private static final int NIBBLES = SAMPLES - 2;
    private static final int MAX_INDEX = 11;

    @Override
    public boolean canDecode(Audio audio, AudioCodec target) {
        return audio.codec() == AudioCodec.WWISE_PTADPCM
            && target == AudioCodec.PCM_S16_LE;
    }

    @Override
    public Audio decode(Audio audio, AudioCodec target) {
        int numChannels = audio.channels().size();
        int frameCount = audio.frameCount();
        int stride = numChannels * Short.BYTES;

        var src = audio.data();
        var dst = Bytes.allocate(frameCount * numChannels * Short.BYTES);

        int numBlocks = frameCount / SAMPLES;
        for (int b = 0; b < numBlocks; b++) {
            int srcOff = b * numChannels * BLOCK;
            int dstOff = b * numChannels * SAMPLES * Short.BYTES;
            for (int i = 0; i < numChannels; i++) {
                decodeBlock(
                    src, srcOff + i * BLOCK,
                    dst, dstOff + i * Short.BYTES,
                    stride
                );
            }
        }

        return new Audio(
            AudioCodec.PCM_S16_LE,
            audio.sampleRate(),
            audio.channels(),
            frameCount,
            dst
        );
    }

    private void decodeBlock(Bytes src, int srcOff, Bytes.Mutable dst, int dstOff, int stride) {
        int hist2 = src.getShort(srcOff);
        int hist1 = src.getShort(srcOff + 2);
        int index = Math.min(src.getUnsigned(srcOff + 4), MAX_INDEX);
        dst.setShort(dstOff, (short) hist2);
        dst.setShort(dstOff + stride, (short) hist1);

        for (int i = 0, o = dstOff + 2 * stride; i < NIBBLES; i++, o += stride) {
            int b = src.getUnsigned(srcOff + 5 + (i >> 1));
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
}
