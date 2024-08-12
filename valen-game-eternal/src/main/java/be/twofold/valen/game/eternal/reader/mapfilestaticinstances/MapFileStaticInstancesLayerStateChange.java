package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MapFileStaticInstancesLayerStateChange(
    String checkpointName,
    String playerSpawnSpot
) {
    public static MapFileStaticInstancesLayerStateChange read(DataSource source) throws IOException {
        var checkpointName = source.readPString();
        var playerSpawnSpot = source.readPString();
        return new MapFileStaticInstancesLayerStateChange(checkpointName, playerSpawnSpot);
    }
}
