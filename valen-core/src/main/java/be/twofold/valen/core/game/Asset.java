package be.twofold.valen.core.game;

import java.util.*;

public record Asset(
    AssetID id,
    AssetType type,
    int size,
    Map<String, Object> properties
) implements Comparable<Asset> {
    public Asset {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        properties = Map.copyOf(properties);
    }

    @Override
    public int compareTo(Asset o) {
        return id.compareTo(o.id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Asset other
            && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
