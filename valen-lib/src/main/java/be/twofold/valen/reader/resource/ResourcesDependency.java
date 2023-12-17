package be.twofold.valen.reader.resource;

import be.twofold.valen.core.util.*;

public record ResourcesDependency(
    int type,
    int name,
    int depType,
    long hashOrTimestamp
) {
    public static final int BYTES = 32;

    public static ResourcesDependency read(BetterBuffer buffer) {
        var type = buffer.getLongAsInt();
        var name = buffer.getLongAsInt();
        var depType = buffer.getInt();
        buffer.expectInt(1); // depSubType
        var hashOrTimestamp = buffer.getLong();

        return new ResourcesDependency(
            type,
            name,
            depType,
            hashOrTimestamp
        );
    }
}
