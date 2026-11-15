package be.twofold.valen.core.audio.decoder;

import be.twofold.tinyaudio.ogg.*;
import be.twofold.valen.core.audio.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public abstract class OggDecoder implements AudioDecoder {
    private static final int BLOCK_SIZE = 4096;

    @Override
    public final Audio decode(Audio audio) throws IOException {
        var head = audio.data().slice(0, 64).toArray();
        try (var decoder = new OggAudioDecoderProvider()
            .open(head, audio.data().asInputStream())
            .orElseThrow(() -> new IOException("No decoder found for " + audio.codec()))) {

            var channelCount = decoder.info().channels();
            if (audio.channels().size() != channelCount) {
                throw new IOException("Channel count mismatch: stream has " + channelCount + ", labels have " + audio.channels().size());
            }

            var order = waveOrder(audio.channels());
            var data = decodeInterleaved(decoder, order, audio.frameCount());
            var channels = Arrays.stream(order)
                .mapToObj(audio.channels()::get)
                .toList();

            return new Audio(
                AudioCodec.PCM_S16_LE,
                audio.sampleRate(),
                channels,
                audio.frameCount(),
                data
            );
        }
    }

    private int[] waveOrder(List<AudioChannel> channels) {
        return IntStream.range(0, channels.size())
            .boxed()
            .sorted(Comparator.comparingInt(c -> channels.get(c).ordinal()))
            .mapToInt(Integer::intValue)
            .toArray();
    }

    private Bytes decodeInterleaved(
        be.twofold.tinyaudio.core.AudioDecoder decoder,
        int[] order,
        int frameCount
    ) throws IOException {
        var channelCount = order.length;
        var stride = channelCount * Short.BYTES;
        var data = Bytes.allocate(frameCount * stride);
        var pcm = new short[channelCount][BLOCK_SIZE];

        var frames = 0;
        while (frames < frameCount) {
            var read = decoder.read(pcm, 0, Math.min(BLOCK_SIZE, frameCount - frames));
            if (read <= 0) {
                break;
            }

            var offset = frames * stride;
            for (var c = 0; c < channelCount; c++) {
                var channel = pcm[order[c]];
                for (int i = 0, o = offset + c * Short.BYTES; i < read; i++, o += stride) {
                    data.setShort(o, channel[i]);
                }
            }
            frames += read;
        }
        return data;
    }
}
