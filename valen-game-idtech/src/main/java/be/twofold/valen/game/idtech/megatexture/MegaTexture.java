package be.twofold.valen.game.idtech.megatexture;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

/**
 * A virtual texture, addressed as a quadtree of pages.
 */
public interface MegaTexture extends Closeable {

    /**
     * The number of mip levels in this texture.
     */
    int levelCount();

    /**
     * The data in a single layer in a single page. Empty if it doesn't exist.
     */
    Optional<BinarySource> findLayer(int level, int x, int y, int layer) throws IOException;

}
