package be.twofold.valen.reader.geometry;

import be.twofold.valen.core.io.*;

import java.io.*;

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
    public static GeometryMemoryLayout read(DataSource source) throws IOException {
        var combinedVertexMask = source.readInt();
        var size = source.readInt();
        var numVertexStreams = source.readInt();
        assert numVertexStreams == 4 || numVertexStreams == 5;

        var positionMask = source.readInt(); // 1 = unpacked, 32 = packed
        var normalMask = source.readInt(); // always 20
        var uvLightMapMask = numVertexStreams == 5 ? source.readInt() : 0; // always 64
        var uvMask = source.readInt(); // 131072 = unpacked, 32768 = packed
        var colorMask = source.readInt(); // always 8

        var positionOffset = source.readInt();
        var normalOffset = source.readInt();
        var uvLightMapOffset = numVertexStreams == 5 ? source.readInt() : 0;
        var uvOffset = source.readInt();
        var colorOffset = source.readInt();

        var indexOffset = source.readInt();

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
