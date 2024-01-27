package be.twofold.valen.resource;

import java.time.*;
import java.util.*;

public record Resource(
    ResourceName name,
    ResourceType type,
    ResourceVariation variation,
    Instant creationTime,
    int offset,
    int size,
    int uncompressedSize,
    long hash,
    List<ResourceDependency> dependencies
) {
    public ResourceKey key() {
        return new ResourceKey(name, type, variation);
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
            "creationTime=" + creationTime + ", " +
            "offset=" + offset + ", " +
            "size=" + size + ", " +
            "uncompressedSize=" + uncompressedSize + ", " +
            "hash=" + hash + ", " +
            "dependencies=[" + dependencies.size() + " dependencies]" +
            "]";
    }
}
