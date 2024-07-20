package be.twofold.valen.resource;

public record Resource(
    ResourceKey key,
    int offset,
    int compressedSize,
    int uncompressedSize,
    CompressionType compression,
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
