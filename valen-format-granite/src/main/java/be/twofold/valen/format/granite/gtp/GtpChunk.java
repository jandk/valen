package be.twofold.valen.format.granite.gtp;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.format.granite.enums.*;

import java.io.*;

public record GtpChunk(
    Codec codec,
    int param,
    int size,
    Bytes data
) {
    static GtpChunk read(BinaryReader reader) throws IOException {
        var codec = Codec.fromValue(reader.readInt());
        var param = reader.readInt();
        var size = reader.readInt();
        var data = reader.readBytes(size);

        return new GtpChunk(codec, param, size, data);
    }
}
