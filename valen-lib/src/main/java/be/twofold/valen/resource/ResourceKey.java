package be.twofold.valen.resource;

import java.util.*;

public record ResourceKey(
    ResourceName name,
    ResourceType type,
    ResourceVariation variation
) implements Comparable<ResourceKey> {
    private static final Comparator<ResourceKey> COMPARATOR = Comparator
        .comparing(ResourceKey::name)
        .thenComparing(ResourceKey::type)
        .thenComparing(ResourceKey::variation);

    @Override
    public int compareTo(ResourceKey o) {
        return COMPARATOR.compare(this, o);
    }
}
