package be.twofold.valen.game.darkages;

import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class DarkAgesAssetID extends IdclAssetID<ResourcesVariation> {
    private static final Set<ResourcesVariation> NONE_SET = EnumSet.of(ResourcesVariation.RES_VAR_NONE);

    private static final Map<ResourceType, Set<ResourcesVariation>> VARIATIONS = Map.ofEntries(
        // Binary decls
        Map.entry(ResourceType.of("entityDef"), Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourceType.of("logicClass"), Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourceType.of("logicEntity"), Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourceType.of("logicFX"), Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourceType.of("logicLibrary"), Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourceType.of("logicUIWidget"), Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourceType.of("mapentities"), Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),

        // Physics
        Map.entry(ResourceType.of("havokcloth"), Set.of(ResourcesVariation.RES_VAR_PLATFORM_WIN64)),
        Map.entry(ResourceType.of("physicsRagdollResource"), Set.of(ResourcesVariation.RES_VAR_PLATFORM_WIN64)),
        Map.entry(ResourceType.of("collisionShape"), Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),
        Map.entry(ResourceType.of("hknavvolumeip"), Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),
        Map.entry(ResourceType.of("mapentitycollisionresources"), Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),
        Map.entry(ResourceType.of("physicsworldcollision"), Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),

        // Shaders
        Map.entry(ResourceType.of("opacitymicromapdata"), Set.of(ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE)),
        Map.entry(ResourceType.of("renderProgResource"), Set.of(
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD_RETAIL,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE_RETAIL
        ))
    );

    DarkAgesAssetID(String name, ResourceType type, ResourcesVariation variation) {
        super(name, type, variation);
    }

    public static DarkAgesAssetID from(String name, ResourceType type) {
        var variations = VARIATIONS
            .getOrDefault(type, NONE_SET);

        Check.argument(variations.size() == 1, "Multiple variations found");
        return new DarkAgesAssetID(name, type, variations.iterator().next());
    }

    public static DarkAgesAssetID material(String name) {
        return from(AssetPaths.MATERIAL2.resolve(name + ".decl").toString(), ResourceType.of("rs_streamfile"));
    }
}
