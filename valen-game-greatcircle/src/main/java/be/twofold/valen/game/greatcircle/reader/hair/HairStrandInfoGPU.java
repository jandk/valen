package be.twofold.valen.game.greatcircle.reader.hair;

import be.twofold.valen.core.io.*;

import java.io.*;

public record HairStrandInfoGPU(
    int idxFirstPoint,
    int numPointsAndStrandIdx
) {
    public static HairStrandInfoGPU read(BinaryReader reader) throws IOException {
        var idxFirstPoint = reader.readInt();
        var numPointsAndStrandIdx = reader.readInt();
        return new HairStrandInfoGPU(idxFirstPoint, numPointsAndStrandIdx);
    }
}
