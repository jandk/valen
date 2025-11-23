package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;
import be.twofold.valen.format.granite.gdex.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record Gts(
    Path path,
    GtsHeader header,
    List<GtsLayer> layers,
    List<GtsLevel> levels,
    List<GtsTile> tiles,
    int[] tileIndex,
    List<GtsPageFile> pageFiles,
    GdexStruct metadata,
    List<GtsParamBlock> paramBlocks,
    List<GtsThumbnail> thumbnails
) {
    public static Gts load(Path path) throws IOException {
        try (var reader = BinaryReader.fromPath(path)) {
            return read(reader, path);
        }
    }

    public static Gts read(BinaryReader reader, Path path) throws IOException {
        var header = GtsHeader.read(reader);
        var layers = reader.position(header.layerOffset()).readObjects(header.layerCount(), GtsLayer::read);
        var levels = reader.position(header.levelOffset()).readObjects(header.levelCount(), r -> GtsLevel.read(r, header.layerCount()));
        var tiles = reader.position(header.tileOffset()).readObjects(header.tileCount(), GtsTile::read);
        var tileIndex = reader.position(header.tileIndexOffset()).readInts(header.tileIndexCount());
        var pageFiles = reader.position(header.pageFileOffset()).readObjects(header.pageFileCount(), r -> GtsPageFile.read(r, header.version()));
        var metadata = reader.position(header.metaOffset()).readObject(Gdex::read).asStruct();
        var paramBlocks = reader.position(header.paramBlockOffset()).readObjects(header.paramBlockCount(), GtsParamBlock::read);

        reader.position(header.thumbnailOffset());
        var thumbnailCount = reader.readInt();
        reader.expectLong(0);
        var thumbnails = reader.readObjects(thumbnailCount, GtsThumbnail::read);

        return new Gts(
            path,
            header,
            layers,
            levels,
            tiles,
            tileIndex,
            pageFiles,
            metadata,
            paramBlocks,
            thumbnails
        );
    }
}
