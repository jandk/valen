package be.twofold.valen.game.colossus;

import be.twofold.valen.game.colossus.reader.resources.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class ColossusAssetID extends IdclAssetID<ResourcesType, ResourcesVariation> {
    private static final Set<ResourcesVariation> NONE_SET = EnumSet.of(ResourcesVariation.RES_VAR_NONE);

    private static final Map<ResourcesType, Set<ResourcesVariation>> VARIATIONS = Map.of(
        // Physics
        ResourcesType.Cm, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),
        ResourcesType.HkNavMesh, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64),
        ResourcesType.HkCloth, Set.of(ResourcesVariation.RES_VAR_PLATFORM_WIN64),

        // Shaders
        ResourcesType.RenderProgResource, Set.of(
            ResourcesVariation.RES_VAR_RENDERPROG_OGL_PC,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_NVIDIA,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD_RPM,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_NVIDIA_TURING
        )
    );

    ColossusAssetID(String name, ResourcesType type, ResourcesVariation variation) {
        super(name, type, variation);
    }

    public static ColossusAssetID from(String name, ResourcesType type) {
        var variations = VARIATIONS
            .getOrDefault(type, NONE_SET);

        Check.state(variations.size() == 1, "Multiple variations found");
        return new ColossusAssetID(name, type, variations.iterator().next());
    }

//    public static ColossusAssetID material(String name) {
//        return from(AssetPaths.MATERIAL2.resolve(name + ".decl").toString(), ResourceType.RsStreamFile);
//    }
}
