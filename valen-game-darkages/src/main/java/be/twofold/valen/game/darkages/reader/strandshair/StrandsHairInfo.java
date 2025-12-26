package be.twofold.valen.game.darkages.reader.strandshair;

import be.twofold.valen.core.io.*;

import java.io.*;

record StrandsHairInfo(
    int idxFirstPoint,
    int numPointsAndStrandIdx
) {
    static StrandsHairInfo read(BinarySource source) throws IOException {
        var idxFirstPoint = source.readInt();
        var numPointsAndStrandIdx = source.readInt();
        return new StrandsHairInfo(idxFirstPoint, numPointsAndStrandIdx);
    }
}
