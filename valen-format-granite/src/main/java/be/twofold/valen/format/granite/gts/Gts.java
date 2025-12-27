package be.twofold.valen.format.granite.gts;

import be.twofold.valen.format.granite.gdex.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public record Gts(
    String path,
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
        Check.nonNull(path, "path");
        Check.nonNull(header, "header");
        layers = List.copyOf(layers);
        levels = List.copyOf(levels);
        tiles = List.copyOf(tiles);
        Check.nonNull(tileIndex, "tileIndex");
        pageFiles = List.copyOf(pageFiles);
        Check.nonNull(metadata, "metadata");
        paramBlocks = List.copyOf(paramBlocks);
        codecHeaders = Map.copyOf(codecHeaders);
        thumbnails = List.copyOf(thumbnails);
    }

    public static Gts read(BinarySource source, String path) throws IOException {
        var header = GtsHeader.read(source);
        var layers = source.position(header.layerOffset()).readObjects(header.layerCount(), GtsLayer::read);
        var levels = source.position(header.levelOffset()).readObjects(header.levelCount(), r -> GtsLevel.read(r, header.layerCount()));
        var tiles = source.position(header.tileOffset()).readObjects(header.tileCount(), GtsTile::read);
        var tileIndex = source.position(header.tileIndexOffset()).readInts(header.tileIndexCount());
        var pageFiles = source.position(header.pageFileOffset()).readObjects(header.pageFileCount(), r -> GtsPageFile.read(r, header.version()));
        var metadata = source.position(header.metaOffset()).readObject(Gdex::read).asStruct();
        var paramBlocks = source.position(header.paramBlockOffset()).readObjects(header.paramBlockCount(), GtsParamBlock::read);

        var codecHeaderDedup = new HashMap<CodecHeader, CodecHeader>();
        var codecHeaders = new HashMap<Integer, CodecHeader>();
        for (var paramBlock : paramBlocks) {
            source.position(paramBlock.offset());
            var codecHeader = codecHeaderDedup.computeIfAbsent(CodecHeader.read(source, paramBlock.codec()), Function.identity());
            codecHeaders.put(paramBlock.id(), codecHeader);
        }

        List<GtsThumbnail> thumbnails = List.of();
        if (header.thumbnailOffset() != 0) {
            source.position(header.thumbnailOffset());
            var thumbnailCount = source.readInt();
            source.expectLong(0);
            thumbnails = source.readObjects(thumbnailCount, GtsThumbnail::read);
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
