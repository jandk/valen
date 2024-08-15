package be.twofold.valen.game.colossus.reader.packagemapspec;

import com.google.gson.*;

import java.util.*;

public record PackageMapSpecMap(
    int chunkId,
    int baseChunkId,
    boolean isBaseChunk,
    int layer,
    String name
) {
    public PackageMapSpecMap {
        Objects.requireNonNull(name);
    }

    public static PackageMapSpecMap read(JsonObject json) {
        var chunkId = json.get("chunkid").getAsInt();
        var baseChunkId = json.get("basechunkid").getAsInt();
        var isBaseChunk = json.get("isbasechunk").getAsBoolean();
        var layer = json.get("layer").getAsInt();
        var name = json.get("name").getAsString();

        return new PackageMapSpecMap(
            chunkId,
            baseChunkId,
            isBaseChunk,
            layer,
            name
        );
    }
}
