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
        return 0x08 + name.getBytes(StandardCharsets.UTF_8).length + arrayLength() * identifier.size();
    }

    public int arrayLength() {
        return value instanceof Buffer
            ? ((Buffer) value).capacity() / identifier.count()
            : 1;
    }
}
