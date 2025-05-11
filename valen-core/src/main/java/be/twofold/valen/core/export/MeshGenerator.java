package be.twofold.valen.core.export;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.math.*;

public final class MeshGenerator {
    private MeshGenerator() {
    }

    public static Mesh createXYPlane(int resX, int resY) {
        var builder = new MeshBuilder();

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

    public static Mesh createCylinder(int resX, int resY) {
        var builder = new MeshBuilder();

        for (int row = 0; row <= resY; row++) {
            float dy = row / (float) resY;
            for (int col = 0; col <= resX; col++) {
                float dx = col / (float) resX;
                float angle = MathF.TAU * dx - MathF.PI;
                float x = MathF.sin(angle);
                float z = MathF.cos(angle);

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

    public static Mesh createSphere(int resX, int resY) {
        var builder = new MeshBuilder();

        for (int row = 0; row <= resY; row++) {
            float dy = row / (float) resY;
            float xz = MathF.sin(MathF.PI * dy);
            float y = -MathF.cos(MathF.PI * dy);
            // float t = y * 0.5f + 0.5f;
            for (int col = 0; col <= resX; col++) {
                float dx = col / (float) resX;
                float angle = MathF.TAU * dx - MathF.PI;
                float x = MathF.sin(angle) * xz;
                float z = MathF.cos(angle) * xz;

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
}
