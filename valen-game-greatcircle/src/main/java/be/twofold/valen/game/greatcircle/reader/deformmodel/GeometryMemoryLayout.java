package be.twofold.valen.game.greatcircle.reader.deformmodel;

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
    int unknown1,
    int unknown2,
    List<MoreOffset> moreOffsets
) implements GeoMemoryLayout {
    static GeometryMemoryLayout read(BinaryReader reader) throws IOException {
        var combinedVertexMask = reader.readInt();
        var size = reader.readInt();
        var numVertexStreams = reader.readInt();
        var vertexMasks = reader.readInts(numVertexStreams);
        var vertexOffsets = reader.readInts(numVertexStreams);
        var indexOffset = reader.readInt();
        var unknown1 = reader.readInt();
        var unknown2 = 0;
        var moreOffsets = (List<MoreOffset>) null;
        if (unknown1 != 0) {
            unknown2 = reader.readInt();
            reader.readObjects(reader.readInt(), MoreOffset::read);
        }

        return new GeometryMemoryLayout(
            combinedVertexMask,
            size,
            numVertexStreams,
            vertexMasks,
            vertexOffsets,
            indexOffset,
            unknown1,
            unknown2,
            moreOffsets
        );
    }

    public record MoreOffset(
        int i1,
        int i2,
        int i3,
        int i4,
        int i5
    ) {
        public static MoreOffset read(BinaryReader reader) throws IOException {
            var i1 = reader.readInt();
            var i2 = reader.readInt();
            var i3 = reader.readInt();
            var i4 = reader.readInt();
            var i5 = reader.readInt();
            return new MoreOffset(i1, i2, i3, i4, i5);
        }
    }
}
