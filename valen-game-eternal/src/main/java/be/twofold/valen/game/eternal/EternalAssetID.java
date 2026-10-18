package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.reader.resources.*;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public record EternalAssetID(
    String name,
    ResourceType type,
    ResourcesVariation variation
) implements AssetID {
    private static final Comparator<EternalAssetID> COMPARATOR = Comparator
        .comparing(EternalAssetID::name)
        .thenComparing(EternalAssetID::type)
        .thenComparing(EternalAssetID::variation);

    private static final Map<ResourceType, Set<ResourcesVariation>> Variations = Map.of(
        ResourceType.HavokShape, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavMesh, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavMeshMediator, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavVolume, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavVolumeMediator, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),
        ResourceType.RenderProgResource, Set.of(
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD_RETAIL,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE_RETAIL
        )
    );

    public static EternalAssetID from(String name, ResourceType type) {
        var variations = Variations
            .getOrDefault(type, Set.of(ResourcesVariation.RES_VAR_NONE));

        Check.state(variations.size() == 1, "Multiple variations found");

        return new EternalAssetID(
            name,
            type,
            variations.iterator().next()
        );
    }

    public static EternalAssetID from(String name, ResourceType type, ResourcesVariation variation) {
        var variationValid = Variations
            .getOrDefault(type, Set.of(ResourcesVariation.RES_VAR_NONE))
            .contains(variation);

        Check.argument(variationValid, "Invalid variation for type: " + type + " (" + variation + ")");
        return new EternalAssetID(name, type, variation);
    }

    public static EternalAssetID material(String name) {
        return from("generated/decls/material2/" + name + ".decl", ResourceType.RsStreamFile);
    }

    @Override
    public String fullName() {
        return name;
    }

    @Override
    public AssetPath path() {
        int index = name.indexOf('$');
        return new AssetPath(index < 0 ? name : name.substring(0, index));
    }

    @Override
    public int compareTo(AssetID o) {
        return COMPARATOR.compare(this, (EternalAssetID) o);
    }
}
