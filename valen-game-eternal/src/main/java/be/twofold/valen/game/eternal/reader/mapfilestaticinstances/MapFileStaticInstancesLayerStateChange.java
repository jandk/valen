package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MapFileStaticInstancesLayerStateChange(
    String checkpointName,
    String playerSpawnSpot
) {
    public static MapFileStaticInstancesLayerStateChange read(BinarySource source) throws IOException {
        var checkpointName = source.readString(StringFormat.INT_LENGTH);
        var playerSpawnSpot = source.readString(StringFormat.INT_LENGTH);
        return new MapFileStaticInstancesLayerStateChange(checkpointName, playerSpawnSpot);
    }
}
