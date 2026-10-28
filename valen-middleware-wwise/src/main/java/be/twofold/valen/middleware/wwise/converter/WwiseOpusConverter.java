package be.twofold.valen.middleware.wwise.converter;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.middleware.wwise.converter.vorbis.*;
import be.twofold.valen.middleware.wwise.wem.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.nio.charset.*;

public final class WwiseOpusConverter implements AudioConverter {
    private static final long OPUS_HEAD = 0x6461_6548_7375_704FL; // "OpusHead"
    private static final long OPUS_TAGS = 0x7367_6154_7375_704FL; // "OpusTags"
    private static final byte[] STREAMS = {2, 2, 3, 4, 4, 5};
    private static final byte[] COUPLED = {1, 2, 2, 2, 3, 3};
    private static final byte[][] CHANNELS = {
        {0, 2, 1},
        {0, 1, 2, 3},
        {0, 4, 1, 2, 3},
        {0, 4, 1, 2, 3, 5},
        {0, 4, 1, 2, 3, 5, 6},
        {0, 6, 1, 2, 3, 4, 5, 7},
    };

    @Override
    public AudioCodec source() {
        return AudioCodec.WWISE_OPUS;
    }

    @Override
    public AudioCodec target() {
        return AudioCodec.OGG_OPUS;
    }

    @Override
    public Audio convert(Audio audio) throws IOException {
        var data = rebuildOpus(audio);
        return new Audio(
            AudioCodec.OGG_OPUS,
            audio.sampleRate(),
            audio.channels(),
            audio.frameCount(),
            data
        );
    }

    private Bytes rebuildOpus(Audio audio) throws IOException {
        var source = BinarySource.wrap(audio.data());
        var wem = Wem.read(source);
        var seek = wem.seek().orElseThrow();
        var opusHeader = (AkOpusWemHeader) wem.format().codecHeader().orElseThrow();

        var family = opusHeader.mappingFamily();
        var channels = wem.format().channels();
        if (family == 0 ? channels > 2 : family != 1 || channels < 3 || channels > 8) {
            throw new IOException("Unsupported Opus mapping family " + family + " with " + channels + " channels");
        }

        var out = new ByteArrayOutputStream();
        var writer = new OggWriter(out);

        writer.addPacket(rebuildOpusHead(wem.format()), 0);
        writer.flushPage();

        writer.addPacket(rebuildOpusTags(), 0);
        writer.flushPage();

        var offset = 0;
        var granule = 0L;
        var payload = wem.data().payload();
        for (int i = 0; i < seek.length(); i++) {
            var size = seek.getUnsigned(i);
            var packet = payload.slice(offset, size);
            offset += size;

            granule += 960;
            if (i == seek.length() - 1) {
                granule = opusHeader.preSkip() + opusHeader.sampleCount();
            }
            writer.addPacket(packet, granule);
        }
        if (offset != payload.length()) {
            throw new IOException("Seek table covers " + offset + " bytes, data chunk has " + payload.length());
        }
        writer.finish();

        return Bytes.wrap(out.toByteArray());
    }

    private Bytes rebuildOpusHead(WwiseFormat format) {
        var opusHeader = (AkOpusWemHeader) format.codecHeader().orElseThrow();
        var size = opusHeader.mappingFamily() == 0 ? 19 : 21 + format.channels();

        var head = ByteBuffer.allocate(size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putLong(OPUS_HEAD)
            .put((byte) 1)
            .put((byte) format.channels())
            .putShort(opusHeader.preSkip())
            .putInt(format.samplesPerSec())
            .putShort((short) 0)
            .put(opusHeader.mappingFamily());

        if (opusHeader.mappingFamily() != 0) {
            int index = format.channels() - 3;
            head.put(STREAMS[index]);
            head.put(COUPLED[index]);
            head.put(CHANNELS[index]);
        }

        return Bytes.wrap(head.flip());
    }

    private Bytes rebuildOpusTags() {
        var vendor = Valen.software().getBytes(StandardCharsets.UTF_8);
        var comment = ("COMMENT=Exported with Valen " + Valen.version()).getBytes(StandardCharsets.UTF_8);

        var tags = ByteBuffer.allocate(8 + 4 + vendor.length + 4 + 4 + comment.length)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putLong(OPUS_TAGS)
            .putInt(vendor.length)
            .put(vendor)
            .putInt(1)
            .putInt(comment.length)
            .put(comment);

        return Bytes.wrap(tags.flip());
    }
}
