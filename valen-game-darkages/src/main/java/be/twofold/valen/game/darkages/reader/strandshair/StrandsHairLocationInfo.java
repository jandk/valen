package be.twofold.valen.game.darkages.reader.strandshair;

import be.twofold.valen.core.io.*;

import java.io.*;

record StrandsHairLocationInfo(
    short baryCentric1,
    short baryCentric2,
    int triangleNormal,
    int triangleSide
) {
    static StrandsHairLocationInfo read(BinaryReader reader) throws IOException {
        var barycentric1 = reader.readShort();
        var barycentric2 = reader.readShort();
        var triangleNormal = reader.readInt();
        var triangleSide = reader.readInt();

        return new StrandsHairLocationInfo(
            barycentric1,
            barycentric2,
            triangleNormal,
            triangleSide
        );
    }
}
