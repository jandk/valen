package be.twofold.valen.game.darkages.reader.strandshair;

import be.twofold.valen.core.io.*;

import java.io.*;

record StrandsHairLocationInfo(
    short baryCentric1,
    short baryCentric2,
    int triangleNormal,
    int triangleSide
) {
    static StrandsHairLocationInfo read(DataSource source) throws IOException {
        var barycentric1 = source.readShort();
        var barycentric2 = source.readShort();
        var triangleNormal = source.readInt();
        var triangleSide = source.readInt();

        return new StrandsHairLocationInfo(
            barycentric1,
            barycentric2,
            triangleNormal,
            triangleSide
        );
    }
}
