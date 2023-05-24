package be.twofold.valen.reader.geometry;

import be.twofold.valen.*;

public record GeometryMemoryLayout(
    int combinedVertexMask,
    int size,
    int numVertexStreams,
    int[] vertexMasks,
    int[] vertexOffsets,
    int indexOffset
) {
    public static GeometryMemoryLayout read(BetterBuffer buffer) {
        int combinedVertexMask = buffer.getInt();
        int size = buffer.getInt();
        int numVertexStreams = buffer.getInt();
        int[] vertexMasks = buffer.getInts(numVertexStreams);
        int[] vertexOffsets = buffer.getInts(numVertexStreams);
        int indexOffset = buffer.getInt();

        return new GeometryMemoryLayout(
            combinedVertexMask,
            size,
            numVertexStreams,
            vertexMasks,
            vertexOffsets,
            indexOffset
        );
    }

    public int getStreamOffset(int mask) {
        for (int i = 0; i < numVertexStreams; i++) {
            if (vertexMasks[i] == mask) {
                return vertexOffsets[i];
            }
        }
        return -1;
    }
}
