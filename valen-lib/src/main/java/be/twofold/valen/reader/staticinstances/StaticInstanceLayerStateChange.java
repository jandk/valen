package be.twofold.valen.reader.staticinstances;

import be.twofold.valen.core.util.*;

public record StaticInstanceLayerStateChange(
    String checkpointName,
    String playerSpawnSpot
) {
    public static StaticInstanceLayerStateChange read(BetterBuffer buffer) {
        var checkpointName = buffer.getString();
        var playerSpawnSpot = buffer.getString();
        return new StaticInstanceLayerStateChange(checkpointName, playerSpawnSpot);
    }
}
