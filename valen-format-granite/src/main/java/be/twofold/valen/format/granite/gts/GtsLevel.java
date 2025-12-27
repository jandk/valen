package be.twofold.valen.format.granite.gts;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record GtsLevel(
    int width,
    int height,
    int offset,
    Ints indices
) {
    public static GtsLevel read(BinarySource source, int layerCount) throws IOException {
        var width = source.readInt();
        var height = source.readInt();
        var offset = source.readLongAsInt();

        // Step out
        var position = source.position();
        var indices = source.position(offset).readInts(width * height * layerCount);
        source.position(position);

        return new GtsLevel(
            width,
            height,
            offset,
            indices
        );
    }
}
