package be.twofold.valen.game.darkages.reader.basemodel;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Md6ModelWoundOffset(
    int vertexIDsOffsets,
    int vertexWeightsOffset,
    int numVertices
) {
    public static Md6ModelWoundOffset read(BinarySource source) throws IOException {
        var vertexIDsOffsets = source.readInt();
        var vertexWeightsOffset = source.readInt();
        var numVertices = source.readInt();

        return new Md6ModelWoundOffset(
            vertexIDsOffsets,
            vertexWeightsOffset,
            numVertices
        );
    }
}
