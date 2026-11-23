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
    private static final byte[] OPUS_HEAD = "OpusHead".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] OPUS_TAGS = "OpusTags".getBytes(StandardCharsets.US_ASCII);

    private static final byte[] STREAMS = {2, 2, 3, 4, 4, 5};
    private static final byte[] COUPLED = {1, 2, 2, 2, 3, 3};
    private static final byte[][] CHANNELS = {
        {0, 2, 1},
        {0, 1, 2, 3},
        {0, 4, 1, 2, 3},
        {0, 4, 1, 2, 3, 5},
        {0, 6, 1, 2, 3, 4, 5},
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
            VorbisChannelOrder.fromWave(audio.channels()),
            audio.frameCount(),
            data
        );
    }

    // AK::Opus::ValidateFormat
    private static int familyOneMask(int channels) {
        return switch (channels) {
            case 3 -> 0x007; // FL FR FC
            case 4 -> 0x603; // FL FR SL SR
            case 5 -> 0x607; // FL FR FC SL SR
            case 6 -> 0x60F; // FL FR FC LFE SL SR
            case 8 -> 0x63F; // FL FR FC LFE BL BR SL SR
            default -> -1;
        };
    }

    private Bytes rebuildOpus(Audio audio) throws IOException {
        var reader = BinaryReader.wrap(audio.data());
        var wem = Wem.read(reader);
        var seek = wem.seek().orElseThrow();
        var opusHeader = (AkOpusWemHeader) wem.format().codecHeader().orElseThrow();

        var family = opusHeader.mappingFamily();
        var channels = wem.format().channels();
        var config = wem.format().channelConfig();
        var isStandard = config.type() == AkChannelConfigType.STANDARD;
        if (family == 0 ? channels > 2 : family != 1 || !isStandard || config.channelMask() != familyOneMask(channels)) {
            throw new IOException(String.format(
                "Unsupported Opus mapping family %s with %s channels (%s)",
                family, channels, config
            ));
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
            .put(OPUS_HEAD)
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
            .put(OPUS_TAGS)
            .putInt(vendor.length)
            .put(vendor)
            .putInt(1)
            .putInt(comment.length)
            .put(comment);

        return Bytes.wrap(tags.flip());
    }
}
