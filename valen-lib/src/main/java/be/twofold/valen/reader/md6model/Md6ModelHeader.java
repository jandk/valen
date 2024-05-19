package be.twofold.valen.reader.md6model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Md6ModelHeader(
    String skelName,
    Bounds bounds,
    boolean regular
) {
    public static Md6ModelHeader read(DataSource source) throws IOException {
        var skelName = source.readPString();
        var bounds = Bounds.read(source);
        var regular = source.readBoolByte(); // true for md6skel, false for alembic
        source.expectInt(0);

        return new Md6ModelHeader(skelName, bounds, regular);
    }
}
