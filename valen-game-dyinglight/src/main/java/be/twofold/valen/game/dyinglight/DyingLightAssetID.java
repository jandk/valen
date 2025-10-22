package be.twofold.valen.game.dyinglight;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.dyinglight.reader.rpack.*;

public record DyingLightAssetID(String name, ResourceType type) implements AssetID {
    @Override
    public String fullName() {
        return name + "_(" + type + ")";
    }
}
