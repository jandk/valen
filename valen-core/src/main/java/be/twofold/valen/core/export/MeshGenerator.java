package be.twofold.valen.core.export;

import be.twofold.valen.core.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.math.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class MeshGenerator {
    private MeshGenerator() {
    }

    public static Mesh.Builder createXYPlane(int resX, int resY) {
        var builder = new Builder();

        for (int row = 0; row <= resY; row++) {
            float y = row / (float) resY;
            for (int col = 0; col <= resX; col++) {
                float x = col / (float) resX;

                builder.addVertex(new Vector3(x, y, 0), new Vector2(x, 1 - y), Vector3.Z);
            }
        }

        for (int row = 0; row < resY; row++) {
            int y0 = (row/**/) * (resX + 1);
            int y1 = (row + 1) * (resX + 1);
            for (int col = 0; col < resX; col++) {
                int x0 = y0 + col;
                int x1 = y1 + col;
                builder.addQuadrangle(x0, x0 + 1, x1, x1 + 1);
            }
        }

        return builder.build();
    }

    public static Mesh.Builder createCylinder(int resX, int resY) {
        var builder = new Builder();

        for (int row = 0; row <= resY; row++) {
            float dy = row / (float) resY;
            for (int col = 0; col <= resX; col++) {
                float dx = col / (float) resX;
                float angle = FloatMath.TAU * dx - FloatMath.PI;
                float x = FloatMath.sin(angle);
                float z = FloatMath.cos(angle);

                Vector3 position = new Vector3(x, dy, z);
                builder.addVertex(position, new Vector2(dx, 1 - dy), position.normalize());
            }
        }

        for (int row = 0; row < resY; row++) {
            int y0 = (row/**/) * (resX + 1);
            int y1 = (row + 1) * (resX + 1);
            for (int col = 0; col < resX; col++) {
                int x0 = y0 + col;
                int x1 = y1 + col;
                builder.addQuadrangle(x0, x0 + 1, x1, x1 + 1);
            }
        }

        return builder.build();
    }

    public static Mesh.Builder createSphere(int resX, int resY) {
        var builder = new Builder();

        for (int row = 0; row <= resY; row++) {
            float dy = row / (float) resY;
            float xz = FloatMath.sin(FloatMath.PI * dy);
            float y = -FloatMath.cos(FloatMath.PI * dy);
            // float t = y * 0.5f + 0.5f;
            for (int col = 0; col <= resX; col++) {
                float dx = col / (float) resX;
                float angle = FloatMath.TAU * dx - FloatMath.PI;
                float x = FloatMath.sin(angle) * xz;
                float z = FloatMath.cos(angle) * xz;

                Vector3 position = new Vector3(x, y, z);
                builder.addVertex(position, new Vector2(dx, 1 - dy), position.normalize());
            }
        }

        for (int row = 0; row < resY; row++) {
            int y0 = (row/**/) * (resX + 1);
            int y1 = (row + 1) * (resX + 1);
            for (int col = 0; col < resX; col++) {
                int x0 = y0 + col;
                int x1 = y1 + col;
                if (row != 0) {
                    builder.addTriangle(x0, x0 + 1, x1);
                }
                if (row != resY - 1) {
                    builder.addTriangle(x1, x0 + 1, x1 + 1);
                }
            }
        }

        return builder.build();
    }

    static final class Builder {
        private static final int INITIAL_CAPACITY = 16;

        private float[] positions = new float[INITIAL_CAPACITY * 3];
        private float[] texCoords = new float[INITIAL_CAPACITY * 2];
        private float[] normals = new float[INITIAL_CAPACITY * 3];
        private int vertexSize = 0;
        private int vertexCapacity = INITIAL_CAPACITY;

        private int[] indices = new int[INITIAL_CAPACITY];
        private int indexSize = 0;

        void addVertex(Vector3 position, Vector2 texCoord, Vector3 normal) {
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

            vertexSize++;
        }

        void addTriangle(int index0, int index1, int index2) {
            Check.index(index0, vertexSize);
            Check.index(index1, vertexSize);
            Check.index(index2, vertexSize);

            if (indexSize > indices.length - 3) {
                indices = Arrays.copyOf(indices, indices.length * 2);
            }

            indices[indexSize++] = index0;
            indices[indexSize++] = index1;
            indices[indexSize++] = index2;

        }

        void addQuadrangle(int index0, int index1, int index2, int index3) {
            addTriangle(index0, index1, index2);
            addTriangle(index2, index1, index3);
        }

        Mesh.Builder build() {
            var positionBuffer = Floats.Mutable.copyOf(positions);
            return Mesh.builder(Ints.Mutable.copyOf(indices, 0, indexSize), positionBuffer.length() / 3)
                .position(positionBuffer)
                .normal(Floats.Mutable.copyOf(normals))
                .addTexCoord(Floats.Mutable.copyOf(texCoords));
        }

        private float squash(float f) {
            return Math.abs(f) < 1e-6f ? 0.0f : f;
        }
    }
}
