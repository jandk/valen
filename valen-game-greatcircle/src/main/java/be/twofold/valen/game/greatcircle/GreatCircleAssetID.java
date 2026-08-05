package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.greatcircle.reader.resources.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.util.*;

public record GreatCircleAssetID(
    ResourceName name,
    ResourceType type,
    ResourcesVariation variation
) implements Comparable<AssetID>, AssetID {
    private static final Comparator<GreatCircleAssetID> COMPARATOR = Comparator
        .comparing(GreatCircleAssetID::name)
        .thenComparing(GreatCircleAssetID::type)
        .thenComparing(GreatCircleAssetID::variation);

    public static GreatCircleAssetID from(String name, ResourceType type) {
        return new GreatCircleAssetID(
            new ResourceName(name),
            type,
            ResourcesVariation.RES_VAR_NONE
        );
    }

    public static GreatCircleAssetID material(String name) {
        return from(name, ResourceType.material2);
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
        return COMPARATOR.compare(this, (GreatCircleAssetID) o);
    }
}
