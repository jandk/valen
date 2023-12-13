package be.twofold.valen.reader.model;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.geometry.*;

public record ModelLodInfo(
    int numVertices,
    int numEdges,
    int flags,
    Vector3 negBounds,
    Vector3 posBounds,
    Vector3 vertexOffset,
    float vertexScale,
    Vector2 uvOffset,
    float uvScale,
    float unkFloat2,
    float unkFloat3
) implements LodInfo {
    public static ModelLodInfo read(BetterBuffer buffer) {
        buffer.expectInt(-2);
        int numVertices = buffer.getInt();
        int numEdges = buffer.getInt();
        int flags = buffer.getInt();
        Vector3 negBounds = Vector3.read(buffer);
        Vector3 posBounds = Vector3.read(buffer);
        Vector3 vertexOffset = Vector3.read(buffer);
        float vertexScale = buffer.getFloat();
        Vector2 uvOffset = Vector2.read(buffer);
        float uvScale = buffer.getFloat();
        buffer.expectInt(0);
        float unkFloat1 = buffer.getFloat();
        float unkFloat2 = buffer.getFloat();
        buffer.expectInt(0x724c4d42);

        return new ModelLodInfo(
            numVertices,
            numEdges,
            flags,
            negBounds,
            posBounds,
            vertexOffset,
            vertexScale,
            uvOffset,
            uvScale,
            unkFloat1,
            unkFloat2
        );
    }

    @Override
    public int numFaces() {
        assert numEdges % 3 == 0;
        return numEdges / 3;
    }
}
