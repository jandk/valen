package be.twofold.valen.game.greatcircle.resource;

import be.twofold.valen.game.greatcircle.reader.resources.*;

public record Resource(
    ResourceKey key,
    long offset,
    int compressedSize,
    int uncompressedSize,
    ResourceCompressionMode compression,
    long hash,
    long checksum,
    int version
) implements Comparable<Resource> {
    @Override
    public int compareTo(Resource o) {
        return key.compareTo(o.key);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Resource other
            && key.equals(other.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
