package be.twofold.valen.game.greatcircle.reader.decl;

import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.resources.*;
import be.twofold.valen.game.greatcircle.resource.*;
import be.twofold.valen.game.idtech.decl.*;

import java.util.*;

public final class DeclReader extends AbstractDeclReader<GreatCircleAssetID, GreatCircleAsset> {
    private static final Set<ResourceType> ValidDeclTypes = Set.of(
        ResourceType.material2
    );

    @Override
    public boolean canRead(GreatCircleAsset key) {
        return ValidDeclTypes.contains(key.id().type());
    }

    @Override
    public GreatCircleAssetID getAssetID(String name, GreatCircleAssetID baseAssetID) {
        return new GreatCircleAssetID(new ResourceName(name), baseAssetID.type(), ResourcesVariation.RES_VAR_NONE);
    }
}
