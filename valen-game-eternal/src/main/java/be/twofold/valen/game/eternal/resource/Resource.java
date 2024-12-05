package be.twofold.valen.game.eternal.resource;

import be.twofold.valen.game.eternal.reader.resource.*;

public record Resource(
    ResourceKey key,
    int offset,
    int compressedSize,
    int uncompressedSize,
    ResourceCompressionMode compression,
    long hash,
    long checksum
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
