package be.twofold.valen.format.cast;

import java.nio.*;
import java.nio.charset.*;
import java.util.*;

public record CastProperty(
    CastPropertyID identifier,
    String name,
    Object value
) {
    public CastProperty {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);
    }

    public int length() {
        int length = 0x08;
        length += name.getBytes(StandardCharsets.UTF_8).length;
        if (identifier == CastPropertyID.STRING) {
            length += ((String) value).getBytes(StandardCharsets.UTF_8).length + 1;
        } else {
            length += arrayLength() * identifier.size();
        }
        return length;
    }

    public int arrayLength() {
        if (!(value instanceof Buffer)) {
            return 1;
        }
        int limit = ((Buffer) value).limit();
        if (limit % identifier.count() != 0) {
            throw new IllegalArgumentException("Limit of buffer is not a multiple of count");
        }
        return limit / identifier.count();
    }
}
