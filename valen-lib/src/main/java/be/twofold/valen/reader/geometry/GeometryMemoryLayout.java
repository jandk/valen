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
        var combinedVertexMask = buffer.getInt();
        var size = buffer.getInt();
        var numVertexStreams = buffer.getInt();
        assert numVertexStreams == 4 || numVertexStreams == 5;

        var positionMask = buffer.getInt(); // 1 = unpacked, 32 = packed
        var normalMask = buffer.getInt(); // always 20
        var uvLightMapMask = numVertexStreams == 5 ? buffer.getInt() : 0; // always 64
        var uvMask = buffer.getInt(); // 131072 = unpacked, 32768 = packed
        var colorMask = buffer.getInt(); // always 8

        var positionOffset = buffer.getInt();
        var normalOffset = buffer.getInt();
        var uvLightMapOffset = numVertexStreams == 5 ? buffer.getInt() : 0;
        var uvOffset = buffer.getInt();
        var colorOffset = buffer.getInt();

        var indexOffset = buffer.getInt();

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
