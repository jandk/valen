package be.twofold.valen.game.greatcircle.reader.hair;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record HairStrandInfoGPU(
    int idxFirstPoint,
    int numPointsAndStrandIdx
) {
    public static HairStrandInfoGPU read(BinarySource source) throws IOException {
        var idxFirstPoint = source.readInt();
        var numPointsAndStrandIdx = source.readInt();
        return new HairStrandInfoGPU(idxFirstPoint, numPointsAndStrandIdx);
    }
}
