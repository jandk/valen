package be.twofold.valen.game.doom.vmtr;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.doom.mega2.*;

import java.io.*;
import java.util.*;

/**
 * Assembles a texture by cutting an entry's rectangle out of the {@code .mega2} page grid.
 */
public final class VmtrAtlas {
    private static final int TILE_SIZE = 128;
    private static final int TILE_BORDER = 4;
    private static final int TILE_USABLE = TILE_SIZE - TILE_BORDER * 2;

    private final List<Mega2File> pageFiles;

    public VmtrAtlas(List<Mega2File> pageFiles) {
        this.pageFiles = List.copyOf(pageFiles);
    }

    public Texture read(VmtrEntry entry, VmtrLayer layer) throws IOException {
        throw new UnsupportedOperationException();
    }
}
