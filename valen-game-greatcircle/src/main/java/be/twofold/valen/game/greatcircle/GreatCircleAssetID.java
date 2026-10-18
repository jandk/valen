package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.greatcircle.reader.resources.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.util.*;

public record GreatCircleAssetID(
    String name,
    ResourceType type,
    ResourcesVariation variation
) implements AssetID {
    private static final Comparator<GreatCircleAssetID> COMPARATOR = Comparator
        .comparing(GreatCircleAssetID::name)
        .thenComparing(GreatCircleAssetID::type)
        .thenComparing(GreatCircleAssetID::variation);

    public static GreatCircleAssetID from(String name, ResourceType type) {
        return new GreatCircleAssetID(name, type, ResourcesVariation.RES_VAR_NONE);
    }

    public static GreatCircleAssetID material(String name) {
        return from(name, ResourceType.material2);
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
        return COMPARATOR.compare(this, (GreatCircleAssetID) o);
    }
}
