package be.twofold.valen.game.eternal.resource;

import be.twofold.valen.game.eternal.reader.resource.*;

public record Resource(
    ResourceKey key,
    int offset,
    int compressedSize,
    int uncompressedSize,
    ResourceCompressionMode compression,
    long hash
) implements Comparable<Resource> {
    public ResourceName name() {
        return key.name();
    }

    public ResourceType type() {
        return key.type();
    }

    public ResourceVariation variation() {
        return key.variation();
    }

    public String nameString() {
        return key.name().toString();
    }

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
