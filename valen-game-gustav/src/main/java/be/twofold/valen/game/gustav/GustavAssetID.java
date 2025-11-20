package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;

public record GustavAssetID(
    String name
) implements AssetID {
    @Override
    public String fullName() {
        return name;
    }

    @Override
    public String displayName() {
        return fileName();
    }
}
