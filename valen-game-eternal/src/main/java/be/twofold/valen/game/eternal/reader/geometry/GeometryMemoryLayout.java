package be.twofold.valen.game.eternal.reader.geometry;

import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record GeometryMemoryLayout(
    int combinedVertexMask,
    int size,
    int numVertexStreams,
    Ints vertexMasks,
    Ints vertexOffsets,
    int indexOffset
) implements GeoMemoryLayout {
    public static GeometryMemoryLayout read(BinarySource source) throws IOException {
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
