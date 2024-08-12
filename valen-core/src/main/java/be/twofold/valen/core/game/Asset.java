package be.twofold.valen.core.game;

import java.util.*;

public record Asset<T extends AssetIdentifier>(
    T identifier,
    AssetType type,
    int size,
    Map<String, Object> properties
) implements Comparable<Asset<T>> {
    @Override
    public int compareTo(Asset<T> o) {
        return identifier.compareTo(o.identifier);
    }
}
