package be.twofold.valen.game.deathloop.image;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ImageMipHeader(
    int level,
    int depthLevel,
    int width,
    int height,
    int dataSize
) {
    public static ImageMipHeader read(DataSource source) throws IOException {
        var level = source.readInt();
        var depthLevel = source.readInt();
        var width = source.readInt();
        var height = source.readInt();
        var dataSize = source.readInt();

        return new ImageMipHeader(
            level,
            depthLevel,
            width,
            height,
            dataSize
        );
    }
}
