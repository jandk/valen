package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.darkages.reader.resources.*;

import java.util.*;

public record DarkAgesAssetID(
    ResourceName name,
    ResourcesType type,
    ResourcesVariation variation
) implements Comparable<AssetID>, AssetID {
    private static final Comparator<DarkAgesAssetID> COMPARATOR = Comparator
        .comparing(DarkAgesAssetID::name)
        .thenComparing(DarkAgesAssetID::type)
        .thenComparing(DarkAgesAssetID::variation);

    private static final Map<ResourcesType, Set<ResourcesVariation>> Variations = Map.ofEntries(
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

    public static DarkAgesAssetID from(String name, ResourcesType type) {
        var resourceName = new ResourceName(name);
        var variations = Variations
            .getOrDefault(type, Set.of(ResourcesVariation.RES_VAR_NONE));

        Check.state(variations.size() == 1, "Multiple variations found");

        return new DarkAgesAssetID(
            resourceName,
            type,
            ResourcesVariation.RES_VAR_NONE
        );
    }

    public static DarkAgesAssetID from(String name, ResourcesType type, ResourcesVariation variation) {
        Check.argument(Variations.getOrDefault(type, Set.of(ResourcesVariation.RES_VAR_NONE)).contains(variation), "Invalid variation for type: " + type + " (" + variation + ")");
        return new DarkAgesAssetID(
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
        return COMPARATOR.compare(this, (DarkAgesAssetID) o);
    }
}
