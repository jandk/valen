package be.twofold.valen.resource;

import java.time.*;
import java.util.*;

public record Resource(
    ResourceName name,
    ResourceType type,
    Instant creationTime,
    int offset,
    int size,
    int uncompressedSize,
    long hash,
    List<ResourceDependency> dependencies
) {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Resource other
            && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Resource[" +
            "name=" + name + ", " +
            "type=" + type + ", " +
            "creationTime=" + creationTime + ", " +
            "offset=" + offset + ", " +
            "size=" + size + ", " +
            "uncompressedSize=" + uncompressedSize + ", " +
            "hash=" + hash + ", " +
            "dependencies=[" + dependencies.size() + " dependencies]" +
            "]";
    }
}
