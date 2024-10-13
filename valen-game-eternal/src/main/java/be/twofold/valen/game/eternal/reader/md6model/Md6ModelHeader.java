package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Md6ModelHeader(
    String md6SkelName,
    Vector3 minBoundsExpansion,
    Vector3 maxBoundsExpansion,
    boolean remapForSkinning
) {
    public static Md6ModelHeader read(DataSource source) throws IOException {
        var md6SkelName = source.readPString();
        var minBoundsExpansion = Vector3.read(source);
        var maxBoundsExpansion = Vector3.read(source);
        var remapForSkinning = source.readBoolByte(); // true for md6skel, false for alembic
        source.expectInt(0);

        return new Md6ModelHeader(
            md6SkelName,
            minBoundsExpansion,
            maxBoundsExpansion,
            remapForSkinning
        );
    }
}
