package be.twofold.valen;

import be.twofold.valen.core.util.*;

public record ResourceIndex2016Entry(
    int id,
    String name1,
    String name2,
    String name3,
    long offset,
    int size,
    int sizeCompressed,
    byte patchFileNumber
) {
    public static ResourceIndex2016Entry read(BetterBuffer buffer, ResourceIndex2016Header header) {
        var id = Integer.reverseBytes(buffer.getInt());
        var name1 = buffer.getString();
        var name2 = buffer.getString();
        var name3 = buffer.getString();

        var offset = Long.reverseBytes(buffer.getLong());
        var size = Integer.reverseBytes(buffer.getInt());
        var sizeCompressed = Integer.reverseBytes(buffer.getInt());

        if (header.version() <= 4) {
            buffer.expectLong(0);
        } else {
            buffer.getInt();
        }
        var patchFileNumber = buffer.getByte();

        return new ResourceIndex2016Entry(
            id,
            name1,
            name2,
            name3,
            offset,
            size,
            sizeCompressed,
            patchFileNumber
        );
    }
}
