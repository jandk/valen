package be.twofold.valen.core.audio.decoder;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.audio.wave.*;
import wtf.reversed.toolbox.collect.*;

public final class MsAdpcmDecoder extends AdpcmDecoder {
    private static final short[] COEF1 = {256, 512, 0, 192, 240, 460, 392};
    private static final short[] COEF2 = {0, -256, 0, 64, 0, -208, -232};
    private static final int[] ADAPT = {
        230, 230, 230, 230, 307, 409, 512, 614,
        768, 614, 512, 409, 307, 230, 230, 230
    };

    @Override
    public AudioCodec source() {
        return AudioCodec.MS_ADPCM;
    }

    @Override
    protected int blockSize(Audio audio) {
        return info(audio).blockAlign();
    }

    @Override
    protected int samplesPerBlock(Audio audio) {
        return info(audio).samplesPerBlock();
    }

    private AudioCodecInfo.MsAdpcm info(Audio audio) {
        if (!(audio.info() instanceof AudioCodecInfo.MsAdpcm info)) {
            throw new IllegalArgumentException("MS ADPCM audio with incorrect codec info");
        }
        return info;
    }

    @Override
    protected void decodeBlock(Bytes src, int numChannels, Bytes.Mutable dst, int dstOff) {
        int samples = WaveFormatAdpcm.samplesPerBlock(src.length(), numChannels);
        switch (numChannels) {
            case 1 -> decodeMono(src, dst, dstOff, samples);
            case 2 -> decodeStereo(src, dst, dstOff, samples);
            default -> throw new IllegalArgumentException("Unsupported number of channels: " + numChannels);
        }
    }

    private void decodeMono(Bytes src, Bytes.Mutable dst, int dstOff, int samples) {
        var c = new Channel(src.getUnsigned(0), src.getShort(1), src.getShort(3), src.getShort(5));

        dst.setShort(dstOff/**/, (short) c.sample2);
        dst.setShort(dstOff + 2, (short) c.sample1);

        for (int i = 0, o = dstOff + 4; i < samples - 2; i++, o += 2) {
            int b = src.getUnsigned(7 + (i >> 1));
            int n = (i & 1) == 0 ? b >> 4 : b & 0x0F;
            dst.setShort(o, c.expand(n));
        }
    }

    private void decodeStereo(Bytes src, Bytes.Mutable dst, int dstOff, int samples) {
        var l = new Channel(src.getUnsigned(0), src.getShort(2), src.getShort(6), src.getShort(10));
        var r = new Channel(src.getUnsigned(1), src.getShort(4), src.getShort(8), src.getShort(12));

        dst.setShort(dstOff/**/, (short) l.sample2);
        dst.setShort(dstOff + 2, (short) r.sample2);
        dst.setShort(dstOff + 4, (short) l.sample1);
        dst.setShort(dstOff + 6, (short) r.sample1);

        for (int i = 0, o = dstOff + 8; i < samples - 2; i++, o += 4) {
            int b = src.getUnsigned(14 + i);
            dst.setShort(o/**/, l.expand(b >> 4));
            dst.setShort(o + 2, r.expand(b & 15));
        }
    }

    private static final class Channel {
        private final int coef1, coef2;
        private int delta, sample1, sample2;

        Channel(int predictor, int delta, int sample1, int sample2) {
            this.coef1 = COEF1[predictor];
            this.coef2 = COEF2[predictor];
            this.delta = delta;
            this.sample1 = sample1;
            this.sample2 = sample2;
        }

        short expand(int n) {
            int predicted = (sample1 * coef1 + sample2 * coef2) >> 8;
            int difference = (n << 28 >> 28) * delta;
            int sample = Math.clamp(predicted + difference, Short.MIN_VALUE, Short.MAX_VALUE);

            delta = Math.max(16, (ADAPT[n] * delta) >> 8);
            sample2 = sample1;
            sample1 = sample;
            return (short) sample;
        }
    }
}
