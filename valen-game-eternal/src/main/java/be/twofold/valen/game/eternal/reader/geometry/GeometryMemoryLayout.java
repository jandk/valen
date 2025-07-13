package be.twofold.valen.game.eternal.reader.geometry;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;

public record GeometryMemoryLayout(
    int combinedVertexMask,
    int size,
    int numVertexStreams,
    int[] vertexMasks,
    int[] vertexOffsets,
    int indexOffset
) implements GeoMemoryLayout {
    public static GeometryMemoryLayout read(BinaryReader reader) throws IOException {
        var combinedVertexMask = reader.readInt();
        var size = reader.readInt();
        var numVertexStreams = reader.readInt();
        var vertexMasks = reader.readInts(numVertexStreams);
        var vertexOffsets = reader.readInts(numVertexStreams);
        var indexOffset = reader.readInt();

        return new GeometryMemoryLayout(
            combinedVertexMask,
            size,
            numVertexStreams,
            vertexMasks,
            vertexOffsets,
            indexOffset
        );
    }
}
