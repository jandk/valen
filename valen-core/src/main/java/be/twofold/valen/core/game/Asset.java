package be.twofold.valen.core.game;

public record Asset<T extends AssetIdentifier>(
    T identifier,
    AssetType type,
    int size
) implements Comparable<Asset<T>> {
    @Override
    public int compareTo(Asset<T> o) {
        return identifier.compareTo(o.identifier);
    }
}
