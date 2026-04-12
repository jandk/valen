package be.twofold.valen.core.texture.pipeline;

import org.slf4j.*;

import java.util.*;
import java.util.stream.*;

final class TileProcessor {
    private static final Logger log = LoggerFactory.getLogger(TileProcessor.class);
    private static final int TILE_SIZE = 256;

    private static final ThreadLocal<float[]> TILE_BUFFER =
        ThreadLocal.withInitial(() -> new float[TILE_SIZE * TILE_SIZE * 4]);

    private TileProcessor() {
    }

    static void process(int width, int height,
                        TileUnpacker unpacker, List<Stage> stages, TilePacker packer) {
        tileStream(width, height)
            .forEach(tile -> processTile(
                width, height,
                tile[0], tile[1],
                unpacker, stages, packer
            ));
    }

    private static Stream<int[]> tileStream(int width, int height) {
        var tilesX = Math.ceilDiv(width, TILE_SIZE);
        var tilesY = Math.ceilDiv(height, TILE_SIZE);
        return IntStream.range(0, tilesX * tilesY)
            .mapToObj(i -> new int[]{i % tilesX, i / tilesX})
            .parallel();
    }

    private static void processTile(
        int width, int height, int tileX, int tileY,
        TileUnpacker unpacker, List<Stage> stages, TilePacker packer
    ) {
        log.info("Processing tile {}/{} on {}", tileX, tileY, Thread.currentThread().getName());
        var tileW = Math.min(TILE_SIZE, width - tileX * TILE_SIZE);
        var tileH = Math.min(TILE_SIZE, height - tileY * TILE_SIZE);
        var pixelCount = tileW * tileH;

        var tile = TILE_BUFFER.get();

        unpacker.unpack(tileX * TILE_SIZE, tileY * TILE_SIZE, tile, tileW, tileH);

        for (var stage : stages) {
            stage.process(tile, pixelCount);
        }

        packer.pack(tile, tileW, tileH, tileX * TILE_SIZE, tileY * TILE_SIZE);
    }
}
