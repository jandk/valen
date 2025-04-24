package be.twofold.valen.game.greatcircle.reader.geometry;

public interface GeoMemoryLayout {

    int combinedVertexMask();

    int size();

    int numVertexStreams();

    int[] vertexMasks();

    int[] vertexOffsets();

    int indexOffset();

}
