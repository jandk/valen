package be.twofold.valen.game.greatcircle.reader.hair;

import be.twofold.valen.core.io.*;

import java.io.*;

public record HairStrandInfoGPU(
    int idxFirstPoint,
    int numPointsAndStrandIdx
) {
    public static HairStrandInfoGPU read(DataSource source) throws IOException {
        var idxFirstPoint = source.readInt();
        var numPointsAndStrandIdx = source.readInt();
        return new HairStrandInfoGPU(idxFirstPoint, numPointsAndStrandIdx);
    }
}
