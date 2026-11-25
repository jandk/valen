package be.twofold.valen.game.doom.readers.common;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.util.*;

/**
 * The geometry of a single model surface. Vertices and indices are kept as one blob, so a
 * {@code MeshReader} can address both.
 */
public record Triangles(
    int numVerts,
    int numIndices,
    Set<VertexMask> vertexMask,
    Vector3 xyzScale,
    Vector3 xyzBias,
    Vector2 stScale,
    Vector2 stBias,
    Bytes buffer,
    Bounds bounds,
    int detailOffset
) {
    public static final int VERTEX_SIZE = 48;

    public static Triangles read(BinaryReader reader) throws IOException {
        var numVerts = reader.readInt();
        var numIndices = reader.readInt();
        int rawVertexMask = reader.readInt();
        if (rawVertexMask != 0x1_801F) {
            throw new UnsupportedOperationException("Unknown vertex mask: " + rawVertexMask);
        }

        var vertexMask = VertexMask.fromValue(rawVertexMask);
        var xyzScale = Vector3.read(reader);
        var xyzBias = Vector3.read(reader);
        var stScale = Vector2.read(reader);
        var stBias = Vector2.read(reader);
        var buffer = reader.readBytes(numVerts * VERTEX_SIZE + numIndices * Short.BYTES);
        var bounds = Bounds.read(reader);
        var detailOffset = reader.readInt();

        return new Triangles(
            numVerts,
            numIndices,
            vertexMask,
            xyzScale,
            xyzBias,
            stScale,
            stBias,
            buffer,
            bounds,
            detailOffset
        );
    }
}
