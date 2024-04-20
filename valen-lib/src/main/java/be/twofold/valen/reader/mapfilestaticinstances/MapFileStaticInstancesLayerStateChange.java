package be.twofold.valen.reader.mapfilestaticinstances;

import be.twofold.valen.core.util.*;

public record MapFileStaticInstancesLayerStateChange(
    String checkpointName,
    String playerSpawnSpot
) {
    public static MapFileStaticInstancesLayerStateChange read(BetterBuffer buffer) {
        var checkpointName = buffer.getString();
        var playerSpawnSpot = buffer.getString();
        return new MapFileStaticInstancesLayerStateChange(checkpointName, playerSpawnSpot);
    }
}
