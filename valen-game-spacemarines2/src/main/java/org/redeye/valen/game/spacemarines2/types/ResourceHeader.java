package org.redeye.valen.game.spacemarines2.types;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record ResourceHeader(String tag, String type, UUID uuid, int toolVersion, int linksCount, int linksListSize) {

    public static ResourceHeader read(DataSource source) throws IOException {
        var tag = source.readString(4);
        var type = source.readString(32);
        var uuid = new UUID(source.readLong(), source.readLong());
        var toolVersion = source.readInt();
        var linksCount = source.readInt();
        var linksListSize = source.readInt();
        if (linksCount > 0) {
            source.skip(linksListSize);
        }
        return new ResourceHeader(tag, type, uuid, toolVersion, linksCount, linksListSize);
    }
}
