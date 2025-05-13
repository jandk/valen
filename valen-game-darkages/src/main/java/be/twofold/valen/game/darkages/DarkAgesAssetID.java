package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
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

//    private static final Map<ResourcesType, Set<ResourcesVariation>> Variations = new EnumMap<>(Map.of(
//        ResourcesType.HavokShape, EnumSet.of(ResourcesVariation.HkMsvc64),
//        ResourcesType.HkNavMesh, EnumSet.of(ResourcesVariation.HkMsvc64),
//        ResourcesType.HkNavMeshMediator, EnumSet.of(ResourcesVariation.HkMsvc64),
//        ResourcesType.HkNavVolume, EnumSet.of(ResourcesVariation.HkMsvc64),
//        ResourcesType.HkNavVolumeMediator, EnumSet.of(ResourcesVariation.HkMsvc64),
//        ResourcesType.RenderProgResource, EnumSet.of(
//            ResourcesVariation.RenderProgVulkanPcAmd,
//            ResourcesVariation.RenderProgVulkanPcAmdRetail,
//            ResourcesVariation.RenderProgVulkanPcBase,
//            ResourcesVariation.RenderProgVulkanPcBaseRetail
//        )
//    ));

    public static DarkAgesAssetID from(String name, ResourcesType type) {
        var resourceName = new ResourceName(name);
//        var variations = Variations
//            .getOrDefault(type, Set.of(ResourcesVariation.None));

//        if (variations.size() > 1) {
//            throw new IllegalArgumentException("Multiple variations found for type: " + type + " (" + variations + ")");
//        }

        return new DarkAgesAssetID(
            resourceName,
            type,
            ResourcesVariation.RES_VAR_NONE
        );
    }

//    public static DarkAgesAssetID from(String name, ResourcesType type, ResourcesVariation variation) {
////        if (!Variations.getOrDefault(type, Set.of(ResourcesVariation.None)).contains(variation)) {
////            throw new IllegalArgumentException("Invalid variation for type: " + type + " (" + variation + ")");
////        }
//        return new DarkAgesAssetID(
//            new ResourceName(name),
//            type,
//            0
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
        return COMPARATOR.compare(this, (DarkAgesAssetID) o);
    }
}
