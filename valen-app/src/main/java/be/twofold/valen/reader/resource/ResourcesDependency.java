package be.twofold.valen.reader.resource;

import be.twofold.valen.core.util.*;

public record ResourcesDependency(
    String type,
    String name,
    int depType,
    long hashOrTimestamp
) {
    static final int Size = 0x20;

    public static ResourcesDependency read(BetterBuffer buffer, String[] pathStrings) {
        String type = pathStrings[buffer.getLongAsInt()];
        String name = pathStrings[buffer.getLongAsInt()];
        int depType = buffer.getInt();
        buffer.expectInt(1); // depSubType
        long hashOrTimestamp = buffer.getLong();

        return new ResourcesDependency(
            type,
            name,
            depType,
            hashOrTimestamp
        );
    }
}
