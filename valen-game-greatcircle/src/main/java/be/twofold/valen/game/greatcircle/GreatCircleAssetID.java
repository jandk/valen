package be.twofold.valen.game.greatcircle;

import be.twofold.valen.game.greatcircle.reader.resources.*;
import be.twofold.valen.game.greatcircle.resource.ResourceType;
import be.twofold.valen.game.idtech.game.*;

public final class GreatCircleAssetID extends IdclAssetID<ResourceType, ResourcesVariation> {
    GreatCircleAssetID(String name, ResourceType type, ResourcesVariation variation) {
        super(name, type, variation);
    }

    public static GreatCircleAssetID from(String name, ResourceType type) {
        return new GreatCircleAssetID(name, type, ResourcesVariation.RES_VAR_NONE);
    }

    public static GreatCircleAssetID material(String name) {
        return from(name, ResourceType.material2);
    }
}
