package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record MapFileStaticInstancesLayerStateChange(
    String checkpointName,
    String playerSpawnSpot
) {
    public static MapFileStaticInstancesLayerStateChange read(BinaryReader reader) throws IOException {
        var checkpointName = reader.readString(StringFormat.INT_LENGTH);
        var playerSpawnSpot = reader.readString(StringFormat.INT_LENGTH);
        return new MapFileStaticInstancesLayerStateChange(checkpointName, playerSpawnSpot);
    }
}
