package be.twofold.valen.game.darkages;

import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.game.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class DarkAgesAssetID extends IdclAssetID<ResourcesType, ResourcesVariation> {
    private static final Set<ResourcesVariation> NONE_SET = EnumSet.of(ResourcesVariation.RES_VAR_NONE);

    private static final Map<ResourcesType, Set<ResourcesVariation>> VARIATIONS = Map.ofEntries(
        // Binary decls
        Map.entry(ResourcesType.EntityDef, Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourcesType.LogicClass, Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourcesType.LogicEntity, Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourcesType.LogicFX, Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourcesType.LogicLibrary, Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourcesType.LogicUIWidget, Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),
        Map.entry(ResourcesType.MapEntities, Set.of(ResourcesVariation.RES_VAR_DECLS_NO_SANITYCHECKS)),

        // Physics
        Map.entry(ResourcesType.HavokCloth, Set.of(ResourcesVariation.RES_VAR_PLATFORM_WIN64)),
        Map.entry(ResourcesType.PhysicsRagdollResource, Set.of(ResourcesVariation.RES_VAR_PLATFORM_WIN64)),
        Map.entry(ResourcesType.CollisionShape, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),
        Map.entry(ResourcesType.HkNavVolumeIp, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),
        Map.entry(ResourcesType.MapEntityCollisionResources, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),
        Map.entry(ResourcesType.PhysicsWorldCollision, Set.of(ResourcesVariation.RES_VAR_HK_MSVC_64)),

        // Shaders
        Map.entry(ResourcesType.OpacityMicroMapData, Set.of(ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE)),
        Map.entry(ResourcesType.RenderProgResource, Set.of(
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_AMD_RETAIL,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE,
            ResourcesVariation.RES_VAR_RENDERPROG_VULKAN_PC_BASE_RETAIL
        ))
    );

    DarkAgesAssetID(String name, ResourcesType type, ResourcesVariation variation) {
        super(name, type, variation);
    }

    public static DarkAgesAssetID from(String name, ResourcesType type) {
        var variations = VARIATIONS
            .getOrDefault(type, NONE_SET);

        Check.argument(variations.size() == 1, "Multiple variations found");
        return new DarkAgesAssetID(name, type, variations.iterator().next());
    }

    public static DarkAgesAssetID material(String name) {
        return from(AssetPaths.MATERIAL2.resolve(name + ".decl").toString(), ResourcesType.RsStreamFile);
    }
}
