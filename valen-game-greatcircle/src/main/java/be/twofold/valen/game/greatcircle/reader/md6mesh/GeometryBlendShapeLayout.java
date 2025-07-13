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
    static GeometryBlendShapeLayout read(BinaryReader reader) throws IOException {
        int meshIndex = reader.readInt();
        int deltaIndexesBufferOffset = reader.readInt();
        int deltaBufferOffset = reader.readInt();
        int unreliableVerticesOffsetMaybe = reader.readInt();
        int unreliableIndicesOffsetMaybe = reader.readInt();
        int trisPerUnreliableVertexOffsetMaybe = reader.readInt();
        int metaTrisPerUnreliableVertexOffset = reader.readInt();
        int unknown2 = reader.readInt();
        int unknown3 = reader.readInt();
        int numMetaTrisPerUnreliableVertex = reader.readInt();

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
