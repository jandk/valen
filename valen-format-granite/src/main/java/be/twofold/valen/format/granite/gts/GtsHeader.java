package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;
import be.twofold.valen.format.granite.util.*;

import java.io.*;
import java.util.*;

public record GtsHeader(
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
    public static GtsHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x47505247);
        reader.expectInt(5);
        reader.expectInt(0);
        var guid = DotNetUtils.guidBytesToUUID(reader.readBytesStruct(16));
        var layerCount = reader.readInt();
        var layerOffset = reader.readLongAsInt();
        var levelCount = reader.readInt();
        var levelOffset = reader.readLongAsInt();
        var tileWidth = reader.readInt();
        var tileHeight = reader.readInt();
        var tileBorder = reader.readInt();
        var maxTileSize = reader.readInt();
        var tileCount = reader.readInt();
        var tileOffset = reader.readLongAsInt();
        reader.expectLong(0);
        var tileIndexCount = reader.readInt();
        var tileIndexOffset = reader.readLongAsInt();
        for (int i = 0; i < 7; i++) {
            reader.expectInt(0);
        }
        var pageSize = reader.readInt();
        var pageFileCount = reader.readInt();
        var pageFileOffset = reader.readLongAsInt();
        var metaSize = reader.readInt();
        var metaOffset = reader.readLongAsInt();
        var paramBlockCount = reader.readInt();
        var paramBlockOffset = reader.readLongAsInt();
        var thumbnailOffset = reader.readLongAsInt();
        for (int i = 0; i < 4; i++) {
            reader.expectInt(0);
        }

        return new GtsHeader(
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
