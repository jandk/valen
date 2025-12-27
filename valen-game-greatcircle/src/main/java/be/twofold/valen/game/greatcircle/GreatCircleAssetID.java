package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.AssetID;
import be.twofold.valen.game.greatcircle.resource.ResourceName;
import be.twofold.valen.game.greatcircle.resource.ResourceType;
import be.twofold.valen.game.greatcircle.resource.ResourceVariation;

import java.util.Comparator;

public record GreatCircleAssetID(
        ResourceName name,
        ResourceType type,
        ResourceVariation variation
) implements Comparable<AssetID>, AssetID {
    private static final Comparator<GreatCircleAssetID> COMPARATOR = Comparator
            .comparing(GreatCircleAssetID::name)
            .thenComparing(GreatCircleAssetID::type)
            .thenComparing(GreatCircleAssetID::variation);

//    private static final Map<ResourceType, Set<ResourceVariation>> Variations = new EnumMap<ResourceType, Set<ResourceVariation>>(Map.of(

    /// /        ResourceType.HavokShape, EnumSet.of(ResourceVariation.HkMsvc64),
    /// /        ResourceType.HkNavMesh, EnumSet.of(ResourceVariation.HkMsvc64),
    /// /        ResourceType.HkNavMeshMediator, EnumSet.of(ResourceVariation.HkMsvc64),
    /// /        ResourceType.HkNavVolume, EnumSet.of(ResourceVariation.HkMsvc64),
    /// /        ResourceType.HkNavVolumeMediator, EnumSet.of(ResourceVariation.HkMsvc64),
    /// /        ResourceType.RenderProgResource, EnumSet.of(
    /// /            ResourceVariation.RenderProgVulkanPcAmd,
    /// /            ResourceVariation.RenderProgVulkanPcAmdRetail,
    /// /            ResourceVariation.RenderProgVulkanPcBase,
    /// /            ResourceVariation.RenderProgVulkanPcBaseRetail
    /// /        )
//    ));
    public static GreatCircleAssetID from(String name, ResourceType type) {
        return new GreatCircleAssetID(
                new ResourceName(name),
                type,
                ResourceVariation.RES_VAR_NONE
        );
    }
    //
    //    public static ResourceKey from(String name, String type, ResourceVariation variation) {
    //        if (!Variations.getOrDefault(type, Set.of(ResourceVariation.RES_VAR_NONE)).contains(variation)) {
    //            throw new IllegalArgumentException("Invalid variation for type: " + type + " (" + variation + ")");
    //        }
    //        return new ResourceKey(
    //            new ResourceName(name),
    //            type,
    //            variation
    //        );
    //    }

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
        return COMPARATOR.compare(this, (GreatCircleAssetID) o);
    }
}
