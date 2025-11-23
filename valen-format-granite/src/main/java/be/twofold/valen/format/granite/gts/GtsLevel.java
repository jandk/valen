package be.twofold.valen.format.granite.gts;

import be.twofold.valen.core.io.*;

import java.io.*;

public record GtsLevel(
    int width,
    int height,
    int offset,
    int[] indices
) {
    public static GtsLevel read(BinaryReader reader, int layerCount) throws IOException {
        var width = reader.readInt();
        var height = reader.readInt();
        var offset = reader.readLongAsInt();

        // Step out
        var position = reader.position();
        var indices = reader.position(offset).readInts(width * height * layerCount);
        reader.position(position);

        return new GtsLevel(
            width,
            height,
            offset,
            indices
        );
    }
}
