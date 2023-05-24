package be.twofold.valen.reader.model;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;

import java.util.*;

public record ModelLodInfo(
    int numVertices,
    int numEdges,
    EnumSet<ModelFlags> flags,
    Vector3 negBounds,
    Vector3 posBounds,
    Vector3 vertexOffset,
    float vertexScale,
    Vector2 uvMapOffset,
    float uvScale,
    float unkFloat2,
    float unkFloat3
) {
    public static ModelLodInfo read(BetterBuffer buffer) {
        buffer.expectInt(-2);
        int numVertices = buffer.getInt();
        int numEdges = buffer.getInt();
        EnumSet<ModelFlags> flags = ModelFlags.fromMask(buffer.getInt());
        Vector3 negBounds = buffer.getVector3();
        Vector3 posBounds = buffer.getVector3();
        Vector3 vertexOffset = buffer.getVector3();
        float vertexScale = buffer.getFloat();
        Vector2 uvMapOffset = buffer.getVector2();
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
            uvMapOffset,
            uvScale,
            unkFloat1,
            unkFloat2
        );
    }
}
