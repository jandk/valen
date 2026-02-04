package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.reader.resource.*;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public record EternalAssetID(
    ResourceName name,
    ResourceType type,
    ResourceVariation variation
) implements Comparable<AssetID>, AssetID {
    private static final Comparator<EternalAssetID> COMPARATOR = Comparator
        .comparing(EternalAssetID::name)
        .thenComparing(EternalAssetID::type)
        .thenComparing(EternalAssetID::variation);

    private static final Map<ResourceType, Set<ResourceVariation>> Variations = Map.of(
        ResourceType.HavokShape, Set.of(ResourceVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavMesh, Set.of(ResourceVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavMeshMediator, Set.of(ResourceVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavVolume, Set.of(ResourceVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavVolumeMediator, Set.of(ResourceVariation.RES_VAR_HK_MSVC_64),
        ResourceType.RenderProgResource, Set.of(
            ResourceVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD,
            ResourceVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD_RETAIL,
            ResourceVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE,
            ResourceVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE_RETAIL
        )
    );

    public static EternalAssetID from(String name, ResourceType type) {
        var resourceName = new ResourceName(name);
        var variations = Variations
            .getOrDefault(type, Set.of(ResourceVariation.RES_VAR_NONE));

        Check.state(variations.size() == 1, "Multiple variations found");

        return new EternalAssetID(
            resourceName,
            type,
            variations.iterator().next()
        );
    }

    public static EternalAssetID from(String name, ResourceType type, ResourceVariation variation) {
        Check.argument(Variations.getOrDefault(type, Set.of(ResourceVariation.RES_VAR_NONE)).contains(variation), "Invalid variation for type: " + type + " (" + variation + ")");
        return new EternalAssetID(
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
    public String displayName() {
        return name.filename();
    }

    @Override
    public String pathName() {
        return name.pathname();
    }

    @Override
    public String fileName() {
        return name.filenameWithoutProperties();
    }

    @Override
    public int compareTo(AssetID o) {
        return COMPARATOR.compare(this, (EternalAssetID) o);
    }
}
