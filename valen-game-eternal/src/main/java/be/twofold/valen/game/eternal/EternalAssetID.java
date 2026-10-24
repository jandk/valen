package be.twofold.valen.game.eternal;

import be.twofold.valen.game.eternal.reader.resources.*;
import be.twofold.valen.game.eternal.resource.ResourceType;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class EternalAssetID extends IdclAssetID<ResourceType, ResourcesVariation> {
    private static final Set<ResourcesVariation> NONE_SET = EnumSet.of(ResourcesVariation.RES_VAR_NONE);

    private static final Map<ResourceType, Set<ResourcesVariation>> VARIATIONS = Map.of(
        // Physics
        ResourceType.HavokShape, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavMesh, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavMeshMediator, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavVolume, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),
        ResourceType.HkNavVolumeMediator, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),

        // Shaders
        ResourceType.RenderProgResource, Set.of(
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD_RETAIL,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE_RETAIL
        )
    );

    EternalAssetID(String name, ResourceType type, ResourcesVariation variation) {
        super(name, type, variation);
    }

    public static EternalAssetID from(String name, ResourceType type) {
        var variations = VARIATIONS
            .getOrDefault(type, NONE_SET);

        Check.state(variations.size() == 1, "Multiple variations found");
        return new EternalAssetID(name, type, variations.iterator().next());
    }

    public static EternalAssetID material(String name) {
        return from(AssetPaths.MATERIAL2.resolve(name + ".decl").toString(), ResourceType.RsStreamFile);
    }
}
