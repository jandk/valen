package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.format.granite.gdex.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

public record Gts(
    Path path,
    GtsHeader header,
    List<GtsLayer> layers,
    List<GtsLevel> levels,
    List<GtsTile> tiles,
    Ints tileIndex,
    List<GtsPageFile> pageFiles,
    GdexStruct metadata,
    List<GtsParamBlock> paramBlocks,
    Map<Integer, CodecHeader> codecHeaders,
    List<GtsThumbnail> thumbnails
) {
    public Gts {
        Check.notNull(path, "path");
        Check.notNull(header, "header");
        layers = List.copyOf(layers);
        levels = List.copyOf(levels);
        tiles = List.copyOf(tiles);
        Check.notNull(tileIndex, "tileIndex");
        pageFiles = List.copyOf(pageFiles);
        Check.notNull(metadata, "metadata");
        paramBlocks = List.copyOf(paramBlocks);
        codecHeaders = Map.copyOf(codecHeaders);
        thumbnails = List.copyOf(thumbnails);
    }

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

        var codecHeaderDedup = new HashMap<CodecHeader, CodecHeader>();
        var codecHeaders = new HashMap<Integer, CodecHeader>();
        for (var paramBlock : paramBlocks) {
            reader.position(paramBlock.offset());
            var codecHeader = codecHeaderDedup.computeIfAbsent(CodecHeader.read(reader, paramBlock.codec()), Function.identity());
            codecHeaders.put(paramBlock.id(), codecHeader);
        }

        List<GtsThumbnail> thumbnails = List.of();
        if (header.thumbnailOffset() != 0) {
            reader.position(header.thumbnailOffset());
            var thumbnailCount = reader.readInt();
            reader.expectLong(0);
            thumbnails = reader.readObjects(thumbnailCount, GtsThumbnail::read);
        }

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
            codecHeaders,
            thumbnails
        );
    }
}
