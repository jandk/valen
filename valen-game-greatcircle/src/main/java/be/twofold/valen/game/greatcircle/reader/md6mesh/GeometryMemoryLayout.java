package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.util.*;

record GeometryMemoryLayout(
        int combinedVertexMask,
        int size,
        int numVertexStreams,
        int[] vertexMasks,
        int[] vertexOffsets,
        int indexOffset,
        List<GeometryBlendShapeLayout> blendShapeLayouts
) implements GeoMemoryLayout {
    static GeometryMemoryLayout read(BinaryReader reader) throws IOException {
        var combinedVertexMask = reader.readInt();
        var size = reader.readInt();
        var numVertexStreams = reader.readInt();
        var vertexMasks = reader.readInts(numVertexStreams);
        var vertexOffsets = reader.readInts(numVertexStreams);
        var indexOffset = reader.readInt();

        int blendSize = reader.readInt();
        var blendShapeLayouts = blendSize != 0
            ? reader.readObjects(reader.readInt(), GeometryBlendShapeLayout::read)
                : List.<GeometryBlendShapeLayout>of();

        return new GeometryMemoryLayout(
                combinedVertexMask,
                size,
                numVertexStreams,
                vertexMasks,
                vertexOffsets,
                indexOffset,
                blendShapeLayouts
        );
    }
}
