package be.twofold.valen.core.export;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.util.*;

final class MeshBuilder {
    private static final int INITIAL_CAPACITY = 16;

    private float[] positions = new float[INITIAL_CAPACITY * 3];
    private float[] texCoords = new float[INITIAL_CAPACITY * 2];
    private float[] normals = new float[INITIAL_CAPACITY * 3];
    private int vertexSize = 0;
    private int vertexCapacity = INITIAL_CAPACITY;

    private int[] indices = new int[INITIAL_CAPACITY];
    private int indexSize = 0;

    int addVertex(Vector3 position, Vector2 texCoord, Vector3 normal) {
        if (vertexSize == vertexCapacity) {
            positions = Arrays.copyOf(positions, positions.length * 2);
            texCoords = Arrays.copyOf(texCoords, texCoords.length * 2);
            normals = Arrays.copyOf(normals, normals.length * 2);
            vertexCapacity *= 2;
        }

        positions[vertexSize * 3/**/] = squash(position.x());
        positions[vertexSize * 3 + 1] = squash(position.y());
        positions[vertexSize * 3 + 2] = squash(position.z());

        texCoords[vertexSize * 2/**/] = squash(texCoord.x());
        texCoords[vertexSize * 2 + 1] = squash(texCoord.y());

        normals[vertexSize * 3/**/] = squash(normal.x());
        normals[vertexSize * 3 + 1] = squash(normal.y());
        normals[vertexSize * 3 + 2] = squash(normal.z());

        return vertexSize++;
    }

    MeshBuilder addTriangle(int index0, int index1, int index2) {
        Check.index(index0, vertexSize);
        Check.index(index1, vertexSize);
        Check.index(index2, vertexSize);

        if (indexSize > indices.length - 3) {
            indices = Arrays.copyOf(indices, indices.length * 2);
        }

        indices[indexSize++] = index0;
        indices[indexSize++] = index1;
        indices[indexSize++] = index2;

        return this;
    }

    MeshBuilder addQuadrangle(int index0, int index1, int index2, int index3) {
        addTriangle(index0, index1, index2);
        addTriangle(index2, index1, index3);

        return this;
    }

    Mesh build() {
        var indexBuffer = new IndexBuffer(Ints.wrap(indices, 0, indexSize));
        var vertexBuffer = new VertexBuffer2(
            Floats.wrap(positions),
            Optional.of(Floats.wrap(normals)),
            Optional.empty(),
            List.of(Floats.wrap(texCoords)),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            0
        );
        return new Mesh(indexBuffer, vertexBuffer);
    }

    private float squash(float f) {
        return Math.abs(f) < 1e-6f ? 0.0f : f;
    }
}
