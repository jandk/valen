package be.twofold.valen.game.eternal.resource;

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

    private static final Map<ResourceType, Set<ResourceVariation>> Variations = new EnumMap<>(Map.of(
        ResourceType.HavokShape, EnumSet.of(ResourceVariation.HkMsvc64),
        ResourceType.HkNavMesh, EnumSet.of(ResourceVariation.HkMsvc64),
        ResourceType.HkNavMeshMediator, EnumSet.of(ResourceVariation.HkMsvc64),
        ResourceType.HkNavVolume, EnumSet.of(ResourceVariation.HkMsvc64),
        ResourceType.HkNavVolumeMediator, EnumSet.of(ResourceVariation.HkMsvc64),
        ResourceType.RenderProgResource, EnumSet.of(
            ResourceVariation.RenderProgVulkanPcAmd,
            ResourceVariation.RenderProgVulkanPcAmdRetail,
            ResourceVariation.RenderProgVulkanPcBase,
            ResourceVariation.RenderProgVulkanPcBaseRetail
        )
    ));

    public static ResourceKey from(String name, ResourceType type) {
        var variations = Variations
            .getOrDefault(type, Set.of(ResourceVariation.None));

        if (variations.size() > 1) {
            throw new IllegalArgumentException("Multiple variations found for type: " + type + " (" + variations + ")");
        }

        return new ResourceKey(
            new ResourceName(name),
            type,
            variations.iterator().next()
        );
    }

    public static ResourceKey from(String name, ResourceType type, ResourceVariation variation) {
        if (!Variations.getOrDefault(type, Set.of(ResourceVariation.None)).contains(variation)) {
            throw new IllegalArgumentException("Invalid variation for type: " + type + " (" + variation + ")");
        }
        return new ResourceKey(
            new ResourceName(name),
            type,
            variation
        );
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
    public int compareTo(AssetIdentifier o) {
        return COMPARATOR.compare(this, (ResourceKey) o);
    }
}
