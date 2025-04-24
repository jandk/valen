package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;

import java.io.*;

record GeometryBlendShapeLayout(
    int meshIndex,
    int deltaIndexesBufferOffset,
    int deltaBufferOffset,
    int unreliableVerticesOffsetMaybe,
    int unreliableIndicesOffsetMaybe,
    int trisPerUnreliableVertexOffsetMaybe,
    int metaTrisPerUnreliableVertexOffset,
    int unknown2,
    int unknown3,
    int numMetaTrisPerUnreliableVertex
) {
    static GeometryBlendShapeLayout read(DataSource source) throws IOException {
        int meshIndex = source.readInt();
        int deltaIndexesBufferOffset = source.readInt();
        int deltaBufferOffset = source.readInt();
        int unreliableVerticesOffsetMaybe = source.readInt();
        int unreliableIndicesOffsetMaybe = source.readInt();
        int trisPerUnreliableVertexOffsetMaybe = source.readInt();
        int metaTrisPerUnreliableVertexOffset = source.readInt();
        int unknown2 = source.readInt();
        int unknown3 = source.readInt();
        int numMetaTrisPerUnreliableVertex = source.readInt();

        return new GeometryBlendShapeLayout(
            meshIndex,
            deltaIndexesBufferOffset,
            deltaBufferOffset,
            unreliableVerticesOffsetMaybe,
            unreliableIndicesOffsetMaybe,
            trisPerUnreliableVertexOffsetMaybe,
            metaTrisPerUnreliableVertexOffset,
            unknown2,
            unknown3,
            numMetaTrisPerUnreliableVertex
        );
    }
}
