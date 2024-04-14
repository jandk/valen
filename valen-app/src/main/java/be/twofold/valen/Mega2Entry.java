package be.twofold.valen;

import be.twofold.valen.core.util.*;

public record Mega2Entry(
    long offset,
    int length
) {
    public static final int BYTES = 16;

    public static Mega2Entry read(BetterBuffer buffer) {
        long offset = buffer.getLong();
        int length = buffer.getLongAsInt();

        return new Mega2Entry(
            offset,
            length
        );
    }
}
