package be.twofold.valen.game.doom.megatexture.vmtr;

import be.twofold.valen.game.doom.mega2.*;

import java.io.*;
import java.util.*;

/**
 * One line of a {@code .vmtr} manifest: where a material sits in the virtual texture atlas.
 */
public record VmtrEntry(
    int x,
    int y,
    int width,
    int height,
    int flags,
    long timeStamp,
    int mtrCheck,
    String name
) {
    public static VmtrEntry read(BufferedReader reader) throws IOException {
        var s = reader.readLine();
        var split = Arrays.stream(s.split("\\s+"))
            .filter(str -> !str.isEmpty())
            .toList();

        var x = Integer.parseInt(split.get(0));
        var y = Integer.parseInt(split.get(1));
        var width = Integer.parseInt(split.get(2));
        var height = Integer.parseInt(split.get(3));
        var flags = Integer.parseInt(split.get(4));
        var timeStamp = Long.parseLong(split.get(5));
        var mtrCheck = Integer.parseInt(split.get(6));
        String name = split.get(7).substring(1, split.get(7).length() - 1);

        return new VmtrEntry(
            x,
            y,
            width,
            height,
            flags,
            timeStamp,
            mtrCheck,
            name
        );
    }

    public int tileX() {
        return x / Mega2Layout.TILE_USABLE;
    }

    public int tileY() {
        return y / Mega2Layout.TILE_USABLE;
    }

    public int tilesW() {
        return Math.ceilDiv(width, Mega2Layout.TILE_USABLE);
    }

    public int tilesH() {
        return Math.ceilDiv(height, Mega2Layout.TILE_USABLE);
    }

    public int coarsestLevel() {
        return Integer.numberOfTrailingZeros(tileX() | tileY() | tilesW() | tilesH());
    }
}
