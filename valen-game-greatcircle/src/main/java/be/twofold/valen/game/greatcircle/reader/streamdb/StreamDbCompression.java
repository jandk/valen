package be.twofold.valen.game.greatcircle.reader.streamdb;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public enum StreamDbCompression implements ValueEnum<Integer> {
    STREAMER_COMPRESSION_NONE_IMAGE(0),
    STREAMER_COMPRESSION_KRAKEN_IMAGE(1),
    STREAMER_COMPRESSION_SCARLETT_TILED_Z(2),
    STREAMER_COMPRESSION_NONE_MODEL(3),
    STREAMER_COMPRESSION_KRAKEN_MODEL(4),
    STREAMER_COMPRESSION_TILED(5),
    STREAMER_COMPRESSION_UNUSED_3(6),
    STREAMER_COMPRESSION_UNUSED_4(7),
    STREAMER_COMPRESSION_NONE_TRIANGLEFAN(8),
    STREAMER_COMPRESSION_KRAKEN_TRIANGLEFAN(9),
    STREAMER_COMPRESSION_RESOURCE_IMAGE(10),
    STREAMER_COMPRESSION_NONE_DIRECT(11),
    STREAMER_COMPRESSION_KRAKEN_DIRECT(12),
    STREAMER_COMPRESSION_NONE_STAGE(13),
    STREAMER_COMPRESSION_KRAKEN_STAGE(14),
    STREAMER_COMPRESSION_KRAKEN_PROGRAM(15),
    STREAMER_COMPRESSION_COUNT(16),
    STREAMER_COMPRESSION_UNSET(255),
    ;

    private final int value;

    StreamDbCompression(int value) {
        this.value = value;
    }

    public static StreamDbCompression read(BinarySource source) throws IOException {
        return ValueEnum.fromValue(StreamDbCompression.class, source.readInt());
    }

    @Override
    public Integer value() {
        return value;
    }
}
