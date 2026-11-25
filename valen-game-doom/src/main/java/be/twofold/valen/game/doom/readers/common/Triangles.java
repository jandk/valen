package be.twofold.valen.game.doom.readers.common;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.geometry.read.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.nio.*;
import java.util.*;

/**
 * The geometry of a single model surface.
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
    private static final int POSITION_OFFSET = 0;
    private static final int TEX_COORD_OFFSET = 12;
    private static final int NORMAL_OFFSET = 20;
    private static final int COLOR_OFFSET = 28;
    // The engine's unit to meter factor (0.75 inch), see idPhysics_AF
    public static final float UNIT_SCALE = 0.01905f;

    public static final int VERTEX_SIZE = 48;
    public static final int VERTEX_MASK = 0x1_801F;

    public static Triangles of(int numVerts, int numIndices, Bytes buffer, Bounds bounds) {
        return new Triangles(
            numVerts,
            numIndices,
            VertexMask.fromValue(VERTEX_MASK),
            Vector3.ONE,
            Vector3.ZERO,
            Vector2.ONE,
            Vector2.ZERO,
            buffer,
            bounds,
            0
        );
    }

    public static Triangles read(BinaryReader reader) throws IOException {
        var numVerts = reader.readInt();
        var numIndices = reader.readInt();
        int rawVertexMask = reader.readInt();
        if (rawVertexMask != VERTEX_MASK) {
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

    public Mesh toMesh(boolean skinned) {
        var builder = MeshFormat.builder(numIndices, numVerts)
            .positions(POSITION_OFFSET, Triangles.VERTEX_SIZE, readPosition(xyzScale, xyzBias))
            .texCoords(0, TEX_COORD_OFFSET, Triangles.VERTEX_SIZE, readTexCoord(stScale, stBias))
            .normals(NORMAL_OFFSET, Triangles.VERTEX_SIZE, readNormal())
            .indices(numVerts * Triangles.VERTEX_SIZE, Short.BYTES, AttributeReader.readShortAsInts());

        if (skinned) {
            builder
                .joints(COLOR_OFFSET, Triangles.VERTEX_SIZE, 4, AttributeReader.copyBytesAsShorts(4))
                .weights(NORMAL_OFFSET, Triangles.VERTEX_SIZE, 4, IdTechGeoReader.readWeights(4));
        }

        var format = builder.build();

        var reader = BinaryReader
            .wrap(buffer)
            .order(ByteOrder.BIG_ENDIAN);

        return new MeshReader(true).readMesh(reader, format);
    }

    private static AttributeReader<Floats.Mutable> readPosition(Vector3 scale, Vector3 bias) {
        return (reader, target, offset) -> Vector3.read(reader)
            .fma(scale, bias)
            .multiply(UNIT_SCALE)
            .toSlice(target, offset);
    }

    private static AttributeReader<Floats.Mutable> readTexCoord(Vector2 scale, Vector2 bias) {
        return (reader, target, offset) -> Vector2.read(reader)
            .fma(scale, bias)
            .toSlice(target, offset);
    }

    private static AttributeReader<Floats.Mutable> readNormal() {
        return (reader, target, offset) -> {
            target.set(offset/**/, MathF.unpackUNorm8Normal(reader.readByte()));
            target.set(offset + 1, MathF.unpackUNorm8Normal(reader.readByte()));
            target.set(offset + 2, MathF.unpackUNorm8Normal(reader.readByte()));
        };
    }
}
