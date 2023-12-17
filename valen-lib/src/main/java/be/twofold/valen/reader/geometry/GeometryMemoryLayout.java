package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.util.*;

public record GeometryMemoryLayout(
    int combinedVertexMask,
    int size,
    int numVertexStreams,
    int positionMask,
    int normalMask,
    int uvMask,
    int uvLightMapMask,
    int colorMask,
    int positionOffset,
    int normalOffset,
    int uvOffset,
    int uvLightMapOffset,
    int colorOffset,
    int indexOffset
) {
    public static GeometryMemoryLayout read(BetterBuffer buffer) {
        int combinedVertexMask = buffer.getInt();
        int size = buffer.getInt();
        int numVertexStreams = buffer.getInt();
        assert numVertexStreams == 4 || numVertexStreams == 5;

        int positionMask = buffer.getInt(); // 1 = unpacked, 32 = packed
        int normalMask = buffer.getInt(); // always 20
        int uvLightMapMask = numVertexStreams == 5 ? buffer.getInt() : 0; // always 64
        int uvMask = buffer.getInt(); // 131072 = unpacked, 32768 = packed
        int colorMask = buffer.getInt(); // always 8

        int positionOffset = buffer.getInt();
        int normalOffset = buffer.getInt();
        int uvLightMapOffset = numVertexStreams == 5 ? buffer.getInt() : 0;
        int uvOffset = buffer.getInt();
        int colorOffset = buffer.getInt();

        int indexOffset = buffer.getInt();

        return new GeometryMemoryLayout(
            combinedVertexMask,
            size,
            numVertexStreams,
            positionMask,
            normalMask,
            uvMask,
            uvLightMapMask,
            colorMask,
            positionOffset,
            normalOffset,
            uvOffset,
            uvLightMapOffset,
            colorOffset,
            indexOffset
        );
    }
}
