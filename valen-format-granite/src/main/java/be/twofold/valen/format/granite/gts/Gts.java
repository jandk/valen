package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;
import be.twofold.valen.format.granite.gdex.*;

import java.io.*;
import java.util.*;

public record Gts(
    GtsHeader header,
    List<GtsLayer> layers,
    List<GtsLevel> levels,
    int[] indices,
    List<GtsTile> tiles,
    int[] tileIndex,
    List<GtsPageFile> pageFiles,
    GdexStruct metadata,
    List<GtsParamBlock> paramBlocks,
    List<GtsThumbnail> thumbnails
) {
    public static Gts read(BinaryReader reader) throws IOException {
        var header = GtsHeader.read(reader);
        var layers = reader.position(header.layerOffset()).readObjects(header.layerCount(), GtsLayer::read);
        var levels = reader.position(header.levelOffset()).readObjects(header.levelCount(), GtsLevel::read);

        int indicesCount = levels.stream()
            .mapToInt(level -> level.width() * level.height() * header.layerCount())
            .sum();
        var indices = reader.readInts(indicesCount);

        var tiles = reader.position(header.tileOffset()).readObjects(header.tileCount(), GtsTile::read);
        var tileIndex = reader.position(header.tileIndexOffset()).readInts(header.tileIndexCount());
        var pageFiles = reader.position(header.pageFileOffset()).readObjects(header.pageFileCount(), GtsPageFile::read);
        var metadata = reader.position(header.metaOffset()).readObject(Gdex::read).asStruct().orElseThrow();
        var paramBlocks = reader.position(header.paramBlockOffset()).readObjects(header.paramBlockCount(), GtsParamBlock::read);

        reader.position(header.thumbnailOffset());
        var thumbnailCount = reader.readInt();
        reader.expectLong(0);
        var thumbnails = reader.readObjects(thumbnailCount, GtsThumbnail::read);

        return new Gts(
            header,
            layers,
            levels,
            indices,
            tiles,
            tileIndex,
            pageFiles,
            metadata,
            paramBlocks,
            thumbnails
        );
    }
}
