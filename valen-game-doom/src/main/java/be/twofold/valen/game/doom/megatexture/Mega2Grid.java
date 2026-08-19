package be.twofold.valen.game.doom.megatexture;

import be.twofold.valen.game.doom.megatexture.mega2.*;
import be.twofold.valen.game.idtech.megatexture.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * The virtual texture atlas, split over a square grid of {@code .mega2} files.
 */
public final class Mega2Grid implements MegaTexture {
    // 16 files on PC, 4 on Switch; the first file's gridLog2 says which.
    private static final String SUB_QUADRANTS = "_vmtr_sq%d.mega2";
    private static final String QUADRANTS = "_vmtr_q%d.mega2";

    public static final int TILE_SIZE = 128;
    public static final int TILE_BORDER = 4;
    public static final int TILE_USABLE = TILE_SIZE - TILE_BORDER * 2;

    private final List<BinarySource> sources;
    private final List<Mega2> tables;
    private final Mega2Header header;
    private final int gridSize;

    private Mega2Grid(List<BinarySource> sources, List<Mega2> tables) {
        Check.argument(!tables.isEmpty(), "a grid needs at least one file");
        Check.argument(sources.size() == tables.size(), "every table needs a source");
        header = tables.getFirst().header();
        gridSize = 1 << header.gridLog2();

        Check.argument(tables.size() == gridSize * gridSize, "Wrong number of files");
        Check.argument(header.virtualXBlockCount() == header.xBlockCount() * gridSize, "x block count mismatch");
        Check.argument(header.virtualYBlockCount() == header.yBlockCount() * gridSize, "y block count mismatch");
        Check.argument(header.virtualXResolution() == header.virtualXBlockCount() * TILE_USABLE, "x resolution mismatch");
        Check.argument(header.virtualYResolution() == header.virtualYBlockCount() * TILE_USABLE, "y resolution mismatch");

        for (var i = 0; i < tables.size(); i++) {
            var file = tables.get(i);
            Check.argument(header.equalGeometry(file.header()), "geometry mismatch");

            var placement = file.levels().getFirst();
            var expectedX = i % gridSize * header.xBlockCount();
            var expectedY = i / gridSize * header.yBlockCount();
            Check.argument(placement.xBlockIndex() == expectedX, "x block placement mismatch");
            Check.argument(placement.yBlockIndex() == expectedY, "y block placement mismatch");
        }

        this.sources = List.copyOf(sources);
        this.tables = List.copyOf(tables);
    }

    public static Mega2Grid open(Path directory) throws IOException {
        var pattern = Files.exists(directory.resolve(String.format(SUB_QUADRANTS, 1)))
            ? SUB_QUADRANTS
            : QUADRANTS;

        var sources = new ArrayList<BinarySource>();
        var tables = new ArrayList<Mega2>();

        var source = BinarySource.open(directory.resolve(pattern.formatted(1)));
        sources.add(source);
        tables.add(Mega2.read(source));
        var gridSize = 1 << tables.getFirst().header().gridLog2();
        for (var i = 2; i <= gridSize * gridSize; i++) {
            source = BinarySource.open(directory.resolve(pattern.formatted(i)));
            sources.add(source);
            tables.add(Mega2.read(source));
        }
        return new Mega2Grid(sources, tables);
    }

    @Override
    public int levelCount() {
        return header.quadtreeLevelCount();
    }

    @Override
    public Optional<BinarySource> findLayer(int level, int x, int y, int layer) throws IOException {
        var page = findPage(level, x, y);
        if (page.isEmpty()) {
            return Optional.empty();
        }

        var source = page.get();
        var pageHeader = Mega2PageHeader.read(source);

        // Cover can either be stored or replicated from a constant
        if (layer == 4 && !pageHeader.hasCover()) {
            return Optional.of(BinarySource.wrap(
                Bytes
                    .allocate(Mega2PageHeader.COVER_SIZE)
                    .fill(pageHeader.coverFill())
            ));
        }

        var size = pageHeader.size(layer);
        if (size == 0) {
            return Optional.empty();
        }

        return Optional.of(source.slice(Mega2PageHeader.BYTES + pageHeader.offset(layer), size));
    }

    private Optional<BinarySource> findPage(int level, int x, int y) {
        Check.index(level, levelCount());

        var xBlockCount = Math.max(1, header.xBlockCount() >> level);
        var yBlockCount = Math.max(1, header.yBlockCount() >> level);

        var fileX = x / xBlockCount;
        var fileY = y / yBlockCount;
        if (x < 0 || y < 0 || fileX >= gridSize || fileY >= gridSize) {
            return Optional.empty();
        }

        var index = fileY * gridSize + fileX;
        return tables.get(index)
            .findPage(level, x % xBlockCount, y % yBlockCount)
            .map(entry -> sources.get(index).slice(entry.offset(), entry.length()));
    }

    @Override
    public void close() throws IOException {
        for (var source : sources) {
            source.close();
        }
    }
}
