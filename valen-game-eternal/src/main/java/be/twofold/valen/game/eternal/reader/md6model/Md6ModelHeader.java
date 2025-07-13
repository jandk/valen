package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Md6ModelHeader(
    String md6SkelName,
    Vector3 minBoundsExpansion,
    Vector3 maxBoundsExpansion,
    boolean remapForSkinning
) {
    public static Md6ModelHeader read(BinaryReader reader) throws IOException {
        var md6SkelName = reader.readPString();
        var minBoundsExpansion = Vector3.read(reader);
        var maxBoundsExpansion = Vector3.read(reader);
        var remapForSkinning = reader.readBoolByte(); // true for md6skel, false for alembic
        reader.expectInt(0);

        return new Md6ModelHeader(
            md6SkelName,
            minBoundsExpansion,
            maxBoundsExpansion,
            remapForSkinning
        );
    }
}
