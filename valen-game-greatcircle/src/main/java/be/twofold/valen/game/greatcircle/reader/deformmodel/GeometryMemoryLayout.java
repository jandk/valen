package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.util.*;

record GeometryMemoryLayout(
    int combinedVertexMask,
    int size,
    int numVertexStreams,
    Ints vertexMasks,
    Ints vertexOffsets,
    int indexOffset,
    int unknown1,
    int unknown2,
    List<MoreOffset> moreOffsets
) implements GeoMemoryLayout {
    static GeometryMemoryLayout read(BinarySource source) throws IOException {
        var combinedVertexMask = source.readInt();
        var size = source.readInt();
        var numVertexStreams = source.readInt();
        var vertexMasks = source.readInts(numVertexStreams);
        var vertexOffsets = source.readInts(numVertexStreams);
        var indexOffset = source.readInt();
        var unknown1 = source.readInt();
        var unknown2 = 0;
        var moreOffsets = (List<MoreOffset>) null;
        if (unknown1 != 0) {
            unknown2 = source.readInt();
            source.readObjects(source.readInt(), MoreOffset::read);
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
        public static MoreOffset read(BinarySource source) throws IOException {
            var i1 = source.readInt();
            var i2 = source.readInt();
            var i3 = source.readInt();
            var i4 = source.readInt();
            var i5 = source.readInt();
            return new MoreOffset(i1, i2, i3, i4, i5);
        }
    }
}
