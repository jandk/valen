package be.twofold.valen.game.idtech.geometry;

import wtf.reversed.toolbox.collect.*;

public interface GeoMemoryLayout {

    int combinedVertexMask();

    int size();

    int numVertexStreams();

    Ints vertexMasks();

    Ints vertexOffsets();

    int indexOffset();

}
