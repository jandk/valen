package be.twofold.valen.game.idtech.geometry;

import be.twofold.valen.core.util.collect.*;

public interface GeoMemoryLayout {

    int combinedVertexMask();

    int size();

    int numVertexStreams();

    Ints vertexMasks();

    Ints vertexOffsets();

    int indexOffset();

}
