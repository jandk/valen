package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record AkOpusWemHeader(
    int sampleCount,
    int packetCount,
    short preSkip,
    byte version,
    byte mappingFamily
) implements CodecHeader {
    public static AkOpusWemHeader read(BinarySource source) throws IOException {
        var sampleCount = source.readInt();
        var packetCount = source.readInt();
        var preSkip = source.readShort();
        var version = source.readByte();
        var mappingFamily = source.readByte();

        return new AkOpusWemHeader(
            sampleCount,
            packetCount,
            preSkip,
            version,
            mappingFamily
        );
    }
}
