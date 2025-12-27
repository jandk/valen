package be.twofold.valen.format.granite.gtp;

import be.twofold.valen.format.granite.enums.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record GtpChunk(
    Codec codec,
    int param,
    int size,
    Bytes data
) {
    static GtpChunk read(BinarySource source) throws IOException {
        var codec = Codec.fromValue(source.readInt());
        var param = source.readInt();
        var size = source.readInt();
        var data = source.readBytes(size);

        return new GtpChunk(codec, param, size, data);
    }
}
