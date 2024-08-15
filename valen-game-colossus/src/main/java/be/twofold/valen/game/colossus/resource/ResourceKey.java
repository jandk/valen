package be.twofold.valen.game.colossus.resource;

import be.twofold.valen.core.game.*;

import java.util.*;

public record ResourceKey(
    ResourceName name,
    ResourceType type,
    ResourceVariation variation
) implements Comparable<AssetID>, AssetID {
    private static final Comparator<ResourceKey> COMPARATOR = Comparator
        .comparing(ResourceKey::name)
        .thenComparing(ResourceKey::type)
        .thenComparing(ResourceKey::variation);

    public static ResourceKey from(String name, ResourceType type) {
        return new ResourceKey(
            new ResourceName(name),
            type,
            ResourceVariation.None
        );
    }

    public static ResourceKey from(String name, ResourceType type, ResourceVariation variation) {
        return new ResourceKey(
            new ResourceName(name),
            type,
            variation
        );
    }

    @Override
    public String fullName() {
        return name.name();
    }

    @Override
    public String pathName() {
        return name.path();
    }

    @Override
    public String fileName() {
        return name.file();
    }

    @Override
    public int compareTo(AssetID o) {
        return COMPARATOR.compare(this, (ResourceKey) o);
    }
}
