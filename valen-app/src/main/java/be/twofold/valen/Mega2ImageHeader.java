package be.twofold.valen;

import be.twofold.valen.core.util.*;

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

    public static Mega2ImageHeader read(BetterBuffer buffer, int size) {
        int magic = Integer.reverseBytes(buffer.getInt());
        short size1 = Short.reverseBytes(buffer.getShort());
        short size2 = Short.reverseBytes(buffer.getShort());
        short size3 = Short.reverseBytes(buffer.getShort());
        short size4 = Short.reverseBytes(buffer.getShort());
        buffer.expectShort(0);
        short size5 = Short.reverseBytes(buffer.getShort());

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
