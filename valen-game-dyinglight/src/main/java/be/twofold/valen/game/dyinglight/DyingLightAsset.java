package be.twofold.valen.game.dyinglight;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.dyinglight.reader.rpack.*;

import java.util.*;

public record DyingLightAsset(
    DyingLightAssetID id,
    RDPFile file,
    List<RDPPart> parts
) implements Asset {
    @Override
    public AssetType type() {
        return AssetType.RAW;
    }

    @Override
    public int size() {
        return parts.stream().mapToInt(RDPPart::size).sum();
    }

    @Override
    public Map<String, Object> properties() {
        return Map.of(
            "Type", file.type()
        );
    }
}
