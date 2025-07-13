package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

public record Md6ModelWoundOffset(
    int vertexIDsOffsets,
    int vertexWeightsOffset,
    int numVertices
) {
    public static Md6ModelWoundOffset read(BinaryReader reader) throws IOException {
        var vertexIDsOffsets = reader.readInt();
        var vertexWeightsOffset = reader.readInt();
        var numVertices = reader.readInt();

        return new Md6ModelWoundOffset(
            vertexIDsOffsets,
            vertexWeightsOffset,
            numVertices
        );
    }
}
