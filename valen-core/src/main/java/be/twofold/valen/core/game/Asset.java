package be.twofold.valen.core.game;

import java.util.*;

public record Asset(
    AssetID id,
    AssetType type,
    int size,
    Map<String, Object> properties
) implements Comparable<Asset> {
    @Override
    public int compareTo(Asset o) {
        return id.compareTo(o.id);
    }
}
