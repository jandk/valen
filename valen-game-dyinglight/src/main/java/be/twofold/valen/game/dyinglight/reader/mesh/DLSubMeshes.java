package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.util.collect.*;

import java.util.*;

public record DLSubMeshes(
    int count,
    Optional<Ints> indexCounts,
    Optional<List<Shorts>> boneMap,
    Optional<Shorts> materialId
) {
    public DLSubMeshes {
        boneMap = boneMap.map(List::copyOf);
    }
}
