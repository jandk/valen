package be.twofold.valen.game.darkages.reader.strandshair;

import be.twofold.valen.core.io.*;

import java.io.*;

record StrandsHairInfo(
    int idxFirstPoint,
    int numPointsAndStrandIdx
) {
    static StrandsHairInfo read(BinaryReader reader) throws IOException {
        var idxFirstPoint = reader.readInt();
        var numPointsAndStrandIdx = reader.readInt();
        return new StrandsHairInfo(idxFirstPoint, numPointsAndStrandIdx);
    }
}
