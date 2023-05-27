package be.twofold.valen.reader.model;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;
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
        Vector3 negBounds = buffer.getVector3();
        Vector3 posBounds = buffer.getVector3();
        Vector3 vertexOffset = buffer.getVector3();
        float vertexScale = buffer.getFloat();
        Vector2 uvOffset = buffer.getVector2();
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
}
