package be.twofold.valen.game.idtech.geometry;

public interface GeoMemoryLayout {

    int combinedVertexMask();

    int size();

    int numVertexStreams();

    int[] vertexMasks();

    int[] vertexOffsets();

    int indexOffset();

}
