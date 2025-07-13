package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MapFileStaticInstancesLayerStateChange(
    String checkpointName,
    String playerSpawnSpot
) {
    public static MapFileStaticInstancesLayerStateChange read(BinaryReader reader) throws IOException {
        var checkpointName = reader.readPString();
        var playerSpawnSpot = reader.readPString();
        return new MapFileStaticInstancesLayerStateChange(checkpointName, playerSpawnSpot);
    }
}
