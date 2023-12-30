package be.twofold.valen.core.texture;

import be.twofold.valen.core.util.*;

public record Surface(
    int width,
    int height,
    byte[] data
) {
    public Surface {
        Check.argument(width > 0, "width must be greater than 0");
        Check.argument(height > 0, "height must be greater than 0");
        Check.notNull(data, "data is null");

        // TODO: check data length
        // Can't do that right now, because we might need the actual format,
        // but even then calculating the actual length is a PITA for a lot of formats
    }

    @Override
    public String toString() {
        return "Surface(" +
            "width=" + width + ", " +
            "height=" + height + ", " +
            "data=[" + data.length + " bytes]" +
            ")";
    }
}
