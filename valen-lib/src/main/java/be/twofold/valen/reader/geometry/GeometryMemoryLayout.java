package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.io.*;

import java.io.*;

public record GeometryMemoryLayout(
    int combinedVertexMask,
    int size,
    int numVertexStreams,
    int[] vertexMasks,
    int[] vertexOffsets,
    int indexOffset
) {
    public static GeometryMemoryLayout read(DataSource source) throws IOException {
        var combinedVertexMask = source.readInt();
        var size = source.readInt();
        var numVertexStreams = source.readInt();
        var vertexMasks = source.readInts(numVertexStreams);
        var vertexOffsets = source.readInts(numVertexStreams);
        var indexOffset = source.readInt();

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
