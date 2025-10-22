package be.twofold.valen.game.dyinglight.reader.mesh;

import java.util.*;

public record DLMesh(
    List<DLMeshLod> lods,
    DLBone parentBone
) {
}
