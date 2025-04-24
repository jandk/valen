package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.greatcircle.reader.geometry.*;

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
    static GeometryMemoryLayout read(DataSource source) throws IOException {
        var combinedVertexMask = source.readInt();
        var size = source.readInt();
        var numVertexStreams = source.readInt();
        var vertexMasks = source.readInts(numVertexStreams);
        var vertexOffsets = source.readInts(numVertexStreams);
        var indexOffset = source.readInt();

        int blendSize = source.readInt();
        var blendShapeLayouts = blendSize != 0
                ? source.readObjects(source.readInt(), GeometryBlendShapeLayout::read)
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
