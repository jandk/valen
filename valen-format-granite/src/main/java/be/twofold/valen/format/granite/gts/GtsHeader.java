package be.twofold.valen.format.granite.gts;

import be.twofold.valen.format.granite.util.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record GtsHeader(
    int version,
    UUID guid,
    int layerCount,
    int layerOffset,
    int levelCount,
    int levelOffset,
    int tileWidth,
    int tileHeight,
    int tileBorder,
    int maxTileSize,
    int tileCount,
    int tileOffset,
    int tileIndexCount,
    int tileIndexOffset,
    int pageSize,
    int pageFileCount,
    int pageFileOffset,
    int metaSize,
    int metaOffset,
    int paramBlockCount,
    int paramBlockOffset,
    int thumbnailOffset
) {
    public static GtsHeader read(BinarySource source) throws IOException {
        source.expectInt(0x47505247);
        var version = source.readInt();
        if (version != 5 && version != 6) {
            throw new UnsupportedOperationException("Unsupported version: " + version);
        }
        source.expectInt(0);
        var guid = DotNetUtils.guidBytesToUUID(source.readBytes(16));
        var layerCount = source.readInt();
        var layerOffset = source.readLongAsInt();
        var levelCount = source.readInt();
        var levelOffset = source.readLongAsInt();
        var tileWidth = source.readInt();
        var tileHeight = source.readInt();
        var tileBorder = source.readInt();
        var maxTileSize = source.readInt();
        var tileCount = source.readInt();
        var tileOffset = source.readLongAsInt();
        source.expectLong(0);
        var tileIndexCount = source.readInt();
        var tileIndexOffset = source.readLongAsInt();
        for (int i = 0; i < 7; i++) {
            source.expectInt(0);
        }
        var pageSize = source.readInt();
        var pageFileCount = source.readInt();
        var pageFileOffset = source.readLongAsInt();
        var metaSize = source.readInt();
        var metaOffset = source.readLongAsInt();
        var paramBlockCount = source.readInt();
        var paramBlockOffset = source.readLongAsInt();
        var thumbnailOffset = source.readLongAsInt();
        for (int i = 0; i < 4; i++) {
            source.expectInt(0);
        }

        return new GtsHeader(
            version,
            guid,
            layerCount,
            layerOffset,
            levelCount,
            levelOffset,
            tileWidth,
            tileHeight,
            tileBorder,
            maxTileSize,
            tileCount,
            tileOffset,
            tileIndexCount,
            tileIndexOffset,
            pageSize,
            pageFileCount,
            pageFileOffset,
            metaSize,
            metaOffset,
            paramBlockCount,
            paramBlockOffset,
            thumbnailOffset
        );
    }
}
