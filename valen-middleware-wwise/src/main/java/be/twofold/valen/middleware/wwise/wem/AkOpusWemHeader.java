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
    public static AkOpusWemHeader read(BinaryReader reader) throws IOException {
        var sampleCount = reader.readInt();
        var packetCount = reader.readInt();
        var preSkip = reader.readShort();
        var version = reader.readByte();
        var mappingFamily = reader.readByte();

        return new AkOpusWemHeader(
            sampleCount,
            packetCount,
            preSkip,
            version,
            mappingFamily
        );
    }
}
