package be.twofold.valen.game.doom.mega2;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Mega2ImageHeader(
    int magic,
    short size1,
    short size2,
    short size3,
    short size4,
    short size5,
    int totalSize,
    int capacity
) {
    public static Mega2ImageHeader read(DataSource source, int size) throws IOException {
        int magic = Integer.reverseBytes(source.readInt());
        short size1 = Short.reverseBytes(source.readShort());
        short size2 = Short.reverseBytes(source.readShort());
        short size3 = Short.reverseBytes(source.readShort());
        short size4 = Short.reverseBytes(source.readShort());
        source.expectShort((short) 0);
        short size5 = Short.reverseBytes(source.readShort());

        var totalSize = size1 + size2 + size3 + size4;
        var capacity = size;

        return new Mega2ImageHeader(
            magic,
            size1,
            size2,
            size3,
            size4,
            size5,
            totalSize,
            capacity
        );
    }
}
