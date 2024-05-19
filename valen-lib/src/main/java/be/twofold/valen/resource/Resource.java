package be.twofold.valen.resource;

public record Resource(
    ResourceName name,
    ResourceType type,
    ResourceVariation variation,
    int offset,
    int compressedSize,
    int uncompressedSize,
    CompressionType compression,
    long hash
) {
    public ResourceKey key() {
        return new ResourceKey(name, type, variation);
    }

    public String nameString() {
        return name.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Resource other
            && name.equals(other.name)
            && type == other.type
            && variation == other.variation;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + variation.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Resource[" +
            "name=" + name + ", " +
            "type=" + type + ", " +
            "variation=" + variation + ", " +
            "offset=" + offset + ", " +
            "compressedSize=" + compressedSize + ", " +
            "uncompressedSize=" + uncompressedSize + ", " +
            "hash=" + hash +
            "]";
    }
}
