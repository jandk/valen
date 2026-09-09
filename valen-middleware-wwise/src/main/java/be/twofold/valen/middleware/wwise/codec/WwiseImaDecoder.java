package be.twofold.valen.middleware.wwise.codec;

import be.twofold.valen.core.audio.*;
import wtf.reversed.toolbox.collect.*;

public final class WwiseImaDecoder implements AudioDecoder {
    // @formatter:off
    private static final int[] STEP = {
            7,     8,   9,      10,    11,    12,    13,    14,
           16,    17,   19,     21,    23,    25,    28,    31,
           34,    37,   41,     45,    50,    55,    60,    66,
           73,    80,   88,     97,   107,   118,   130,   143,
          157,   173,   190,   209,   230,   253,   279,   307,
          337,   371,   408,   449,   494,   544,   598,   658,
          724,   796,   876,   963,  1060,  1166,  1282,  1411,
         1552,  1707,  1878,  2066,  2272,  2499,  2749,  3024,
         3327,  3660,  4026,  4428,  4871,  5358,  5894,  6484,
         7132,  7845,  8630,  9493, 10442, 11487, 12635, 13899,
        15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794,
        32767
    };
    private static final int[] NEXT = {
        -1, -1, -1, -1, +2, +4, +6, +8,
        -1, -1, -1, -1, +2, +4, +6, +8
    };
    // @formatter:on

    private static final int BLOCK = 36;
    private static final int SAMPLES = 64;
    private static final int NIBBLES = SAMPLES - 1;

    @Override
    public boolean canDecode(Audio audio, AudioCodec target) {
        return audio.codec() == AudioCodec.ADPCM_IMA_WWISE;
    }

    @Override
    public Audio decode(Audio audio, AudioCodec target) {
        int channels = audio.channels();
        int frameCount = audio.frameCount();
        int stride = channels * Short.BYTES;

        var src = audio.data();
        var dst = Bytes.allocate(frameCount * channels * Short.BYTES);

        int numBlocks = frameCount / SAMPLES;
        for (int b = 0; b < numBlocks; b++) {
            int srcOff = b * channels * BLOCK;
            int dstOff = b * channels * SAMPLES * Short.BYTES;
            for (int i = 0; i < channels; i++) {
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
            channels,
            frameCount,
            dst
        );
    }

    private void decodeBlock(Bytes src, int srcOff, Bytes.Mutable dst, int dstOff, int stride) {
        int pred = src.getShort(srcOff);
        int index = src.getUnsigned(srcOff + 2);
        dst.setShort(dstOff, (short) pred);

        for (int i = 0, o = dstOff + stride; i < NIBBLES; i++, o += stride) {
            int b = src.getUnsigned(srcOff + 4 + (i >> 1));
            int n = (i & 1) == 0 ? b & 0x0F : b >> 4;
            int diff = (STEP[index] * (((n & 7) << 1) + 1)) >> 3;
            diff = (n & 8) == 0 ? diff : -diff;
            pred = Math.clamp(pred + diff, Short.MIN_VALUE, Short.MAX_VALUE);
            index = Math.clamp(index + NEXT[n], 0, 88);
            dst.setShort(o, (short) pred);
        }
    }
}
