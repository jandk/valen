package be.twofold.valen.core.audio.decoder;

import be.twofold.valen.core.audio.*;
import wtf.reversed.toolbox.collect.*;

public abstract class AdpcmDecoder implements AudioDecoder {
    @Override
    public final Audio decode(Audio audio) {
        int numChannels = audio.channels().size();
        int frameCount = audio.frameCount();
        int stride = numChannels * Short.BYTES;

        int blockSize = blockSize(audio);
        int samplesPerBlock = samplesPerBlock(audio);
        int blockStride = samplesPerBlock * stride;

        var src = audio.data();
        var dst = Bytes.allocate(frameCount * stride);

        int numBlocks = Math.min(frameCount / samplesPerBlock, src.length() / blockSize);
        int srcOff = 0;
        int dstOff = 0;
        for (int b = 0; b < numBlocks; b++, srcOff += blockSize, dstOff += blockStride) {
            decodeBlock(src.slice(srcOff, blockSize), numChannels, dst, dstOff);
        }

        // Partial block
        int remaining = frameCount - numBlocks * samplesPerBlock;
        if (remaining > 0 && srcOff < src.length()) {
            var block = Bytes.allocate(blockSize);
            src.slice(srcOff, Math.min(blockSize, src.length() - srcOff)).copyTo(block, 0);

            var scratch = Bytes.allocate(blockStride);
            decodeBlock(block, numChannels, scratch, 0);
            scratch.slice(0, Math.min(remaining, samplesPerBlock) * stride).copyTo(dst, dstOff);
        }

        return new Audio(
            AudioCodec.PCM_S16_LE,
            audio.sampleRate(),
            audio.channels(),
            frameCount,
            dst
        );
    }

    protected void decodeBlock(Bytes src, int numChannels, Bytes.Mutable dst, int dstOff) {
        int size = src.length() / numChannels;
        int stride = numChannels * Short.BYTES;
        for (int i = 0; i < numChannels; i++, dstOff += Short.BYTES) {
            decodeChannel(src.slice(i * size, size), dst, dstOff, stride);
        }
    }

    protected void decodeChannel(Bytes src, Bytes.Mutable dst, int dstOff, int stride) {
        throw new UnsupportedOperationException();
    }

    protected abstract int blockSize(Audio audio);

    protected abstract int samplesPerBlock(Audio audio);
}
