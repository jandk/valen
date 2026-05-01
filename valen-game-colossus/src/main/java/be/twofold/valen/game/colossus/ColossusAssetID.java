package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.colossus.resource.*;

import java.util.*;

public record ColossusAssetID(
    ResourceName name,
    ResourceType type,
    ResourceVariation variation
) implements AssetID {
    private static final Comparator<ColossusAssetID> COMPARATOR = Comparator
        .comparing(ColossusAssetID::name)
        .thenComparing(ColossusAssetID::type)
        .thenComparing(ColossusAssetID::variation);

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
}
