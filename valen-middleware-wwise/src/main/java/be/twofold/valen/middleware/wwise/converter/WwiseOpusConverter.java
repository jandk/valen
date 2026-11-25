package be.twofold.valen.middleware.wwise.converter;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.middleware.wwise.converter.vorbis.*;
import be.twofold.valen.middleware.wwise.wem.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.charset.*;

public final class WwiseOpusConverter implements AudioConverter {
    private static final Bytes OPUS_HEAD = Bytes.wrap("OpusHead".getBytes(StandardCharsets.US_ASCII));
    private static final Bytes OPUS_TAGS = Bytes.wrap("OpusTags".getBytes(StandardCharsets.US_ASCII));

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

        var writer = new BytesBinaryWriter();
        var oggWriter = new OggWriter(writer);

        oggWriter.addPacket(rebuildOpusHead(wem.format()), 0);
        oggWriter.flushPage();

        oggWriter.addPacket(rebuildOpusTags(), 0);
        oggWriter.flushPage();

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
            oggWriter.addPacket(packet, granule);
        }
        if (offset != payload.length()) {
            throw new IOException("Seek table covers " + offset + " bytes, data chunk has " + payload.length());
        }
        oggWriter.finish();

        return writer.toBytes();
    }

    private Bytes rebuildOpusHead(WwiseFormat format) {
        var opusHeader = (AkOpusWemHeader) format.codecHeader().orElseThrow();

        var writer = new BytesBinaryWriter();
        writer.writeBytes(OPUS_HEAD);
        writer.writeByte((byte) 1);
        writer.writeByte((byte) format.channels());
        writer.writeShort(opusHeader.preSkip());
        writer.writeInt(format.samplesPerSec());
        writer.writeShort((short) 0);
        writer.writeByte(opusHeader.mappingFamily());

        if (opusHeader.mappingFamily() != 0) {
            int index = format.channels() - 3;
            writer.writeByte(STREAMS[index]);
            writer.writeByte(COUPLED[index]);
            writer.writeBytes(Bytes.wrap(CHANNELS[index]));
        }

        return writer.toBytes();
    }

    private Bytes rebuildOpusTags() throws IOException {
        var writer = new BytesBinaryWriter();
        writer.writeBytes(OPUS_TAGS);
        writer.writeString(Valen.software(), StringFormat.INT_LENGTH);
        writer.writeInt(1);
        writer.writeString("COMMENT=Exported with Valen " + Valen.version(), StringFormat.INT_LENGTH);
        return writer.toBytes();
    }
}
