package be.twofold.valen.game.greatcircle.reader.decl;

import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.resource.*;
import be.twofold.valen.game.idtech.reader.decl.*;

import java.util.*;

public final class DeclReader extends AbstractDeclReader<GreatCircleAssetID, GreatCircleAsset> {
    private static final Set<ResourceType> ValidDeclTypes = Set.of(
        ResourceType.material2
    );

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return ValidDeclTypes.contains(asset.id().type());
    }

    @Override
    public GreatCircleAssetID getAssetID(String name, GreatCircleAssetID baseAssetID) {
        return GreatCircleAssetID.from(name, baseAssetID.type());
    }
}
