package be.twofold.valen.resource;

import be.twofold.valen.core.game.*;

import java.util.*;

public record ResourceKey(
    ResourceName name,
    ResourceType type,
    ResourceVariation variation
) implements Comparable<AssetIdentifier>, AssetIdentifier {
    private static final Comparator<ResourceKey> COMPARATOR = Comparator
        .comparing(ResourceKey::name)
        .thenComparing(ResourceKey::type)
        .thenComparing(ResourceKey::variation);

    @Override
    public int compareTo(AssetIdentifier o) {
        return COMPARATOR.compare(this, (ResourceKey) o);
    }

    @Override
    public String fileName() {
        return name.file();
    }

    @Override
    public String pathName() {
        return name.path();
    }
}
