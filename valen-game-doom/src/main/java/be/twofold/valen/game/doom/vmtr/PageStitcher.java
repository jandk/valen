package be.twofold.valen.game.doom.vmtr;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.doom.megatexture.*;
import be.twofold.valen.game.doom.megatexture.vmtr.*;
import be.twofold.valen.game.idtech.decoder.*;
import be.twofold.valen.game.idtech.megatexture.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

/**
 * Assembles a texture by cutting a rectangle out of a mega texture, tile by tile, at the finest
 * level that still holds the layer.
 */
public final class PageStitcher {
    // Borrowed; closing stays with whoever opened it.
    private final MegaTexture megaTexture;

    public PageStitcher(MegaTexture megaTexture) {
        this.megaTexture = Check.nonNull(megaTexture, "megaTexture");
    }

    public Texture read(VmtrEntry entry, VmtrLayer layer) throws IOException {
        var rect = new Rect(entry.x(), entry.y(), entry.width(), entry.height());

        var level = finestLevel(rect, layer);
        if (level.isPresent()) {
            return stitch(rect, layer, level.getAsInt());
        }

        // A missing colormask or cover is ordinary: only tintable multiplayer gear has the one, and
        // only cutout materials the other.
        if (layer != VmtrLayer.COLORMASK && layer != VmtrLayer.COVER) {
            throw new IOException("No level holds the " + layer + " layer of " + entry.name());
        }
        return Surface
            .create(rect.width(), rect.height(), 1, layer.format())
            .toTexture();
    }

    private OptionalInt finestLevel(Rect rect, VmtrLayer layer) throws IOException {
        // Look at the coarsest level first, this saves quite a few reads.when checking if a layer is actually present.
        // Looking at you colormask...
        var coarsestLevel = coarsestLevel(rect);
        if (!holdsLayer(rect, layer, coarsestLevel)) {
            return OptionalInt.empty();
        }

        for (var level = 0; level < coarsestLevel; level++) {
            if (holdsLayer(rect, layer, level)) {
                return OptionalInt.of(level);
            }
        }
        return OptionalInt.of(coarsestLevel);
    }

    private boolean holdsLayer(Rect rect, VmtrLayer layer, int level) throws IOException {
        for (var y = 0; y < tilesH(rect, level); y++) {
            for (var x = 0; x < tilesW(rect, level); x++) {
                if (payload(rect, layer, level, x, y).isPresent()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Texture stitch(Rect rect, VmtrLayer layer, int level) throws IOException {
        var target = Surface.create(rect.width() >> level, rect.height() >> level, 1, layer.format());
        try {
            IntStream.range(0, tilesW(rect, level) * tilesH(rect, level)).parallel()
                .forEach(i -> copyTile(rect, layer, level, i, target));
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
        return target.toTexture();
    }

    private void copyTile(Rect rect, VmtrLayer layer, int level, int index, Surface target) {
        var x = index % tilesW(rect, level);
        var y = index / tilesW(rect, level);

        Surface tile;
        try {
            var found = payload(rect, layer, level, x, y);
            if (found.isEmpty()) {
                return;
            }

            var source = found.get();
            var payload = source.readBytes(Math.toIntExact(source.size()));
            tile = decodeTile(payload, layer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        var usable = Mega2Grid.TILE_USABLE;
        Surface.copy(
            tile, Mega2Grid.TILE_BORDER, Mega2Grid.TILE_BORDER,
            target, x * usable, y * usable,
            Math.min(usable, (rect.width() >> level) - x * usable),
            Math.min(usable, (rect.height() >> level) - y * usable)
        );
    }

    private Surface decodeTile(Bytes payload, VmtrLayer layer) throws IOException {
        var size = Mega2Grid.TILE_SIZE;
        var transcoder = switch (layer) {
            case DIFFUSE, SPECULAR, LIGHTMAP -> new HdpDecoder(true, layer != VmtrLayer.LIGHTMAP);
            case COLORMASK -> new Lz4BlockDecoder(TextureFormat.BC7_UNORM);
            case COVER -> new BitmaskDecoder(TextureFormat.R8_UNORM);
        };

        return transcoder.decode(payload, size, size);
    }

    private Optional<BinarySource> payload(Rect rect, VmtrLayer layer, int level, int x, int y) throws IOException {
        return megaTexture.findLayer(
            level,
            tileX(rect, level) + x,
            tileY(rect, level) + y,
            layer.slot()
        );
    }

    // The coarsest level the rectangle still lines up with the grid at.
    private static int coarsestLevel(Rect rect) {
        return Integer.numberOfTrailingZeros(
            tileX(rect, 0) | tileY(rect, 0) | tilesW(rect, 0) | tilesH(rect, 0));
    }

    private static int tileX(Rect rect, int level) {
        return rect.x() / Mega2Grid.TILE_USABLE >> level;
    }

    private static int tileY(Rect rect, int level) {
        return rect.y() / Mega2Grid.TILE_USABLE >> level;
    }

    private static int tilesW(Rect rect, int level) {
        return Math.ceilDiv(rect.width(), Mega2Grid.TILE_USABLE) >> level;
    }

    private static int tilesH(Rect rect, int level) {
        return Math.ceilDiv(rect.height(), Mega2Grid.TILE_USABLE) >> level;
    }
}
