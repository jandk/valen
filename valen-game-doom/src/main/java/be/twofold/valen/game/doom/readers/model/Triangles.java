package be.twofold.valen.game.doom.readers.model;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record Triangles(
    int numVerts,
    int numIndices,
    Set<VertexMask> vertexMask,
    Vector3 xyzScale,
    Vector3 xyzBias,
    Vector2 stScale,
    Vector2 stBias,
    Bytes vertexBuffer,
    Shorts indexBuffer,
    Bounds bounds,
    int detailOffset
) {
    public static Triangles read(BinarySource source) throws IOException {
        source.order(ByteOrder.BIG_ENDIAN);

        var numVerts = source.readInt();
        var numIndices = source.readInt();
        int rawVertexMask = source.readInt();
        if (rawVertexMask != 98335) {
            throw new UnsupportedOperationException("Unknown vertex mask: " + rawVertexMask);
        }

        var vertexMask = VertexMask.fromValue(rawVertexMask);
        var xyzScale = Vector3.read(source);
        var xyzBias = Vector3.read(source);
        var stScale = Vector2.read(source);
        var stBias = Vector2.read(source);
        var vertexBuffer = source.readBytes(numVerts * 48);
        var indexBuffer = source.readShorts(numIndices);
        var bounds = Bounds.read(source);
        var detailOffset = source.readInt();

        return new Triangles(
            numVerts,
            numIndices,
            vertexMask,
            xyzScale,
            xyzBias,
            stScale,
            stBias,
            vertexBuffer,
            indexBuffer,
            bounds,
            detailOffset
        );
    }
}
