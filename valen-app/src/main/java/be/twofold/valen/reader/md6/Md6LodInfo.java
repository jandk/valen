package be.twofold.valen.reader.md6;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.geometry.*;

public record Md6LodInfo(
    int numVertices,
    int numFaces,
    Vector3 negBounds,
    Vector3 posBounds,
    Vector3 vertexOffset,
    float vertexScale,
    Vector2 uvOffset,
    float uvScale,
    int flags,
    float unkFloat2,
    float unkFloat3
) implements LodInfo {
    public static Md6LodInfo read(BetterBuffer buffer) {
        int numVertices = buffer.getInt();
        int numFaces = buffer.getInt();
        Vector3 negBounds = Vector3.read(buffer);
        Vector3 posBounds = Vector3.read(buffer);
        Vector3 vertexOffset = Vector3.read(buffer);
        float vertexScale = buffer.getFloat();
        Vector2 uvOffset = Vector2.read(buffer);
        float uvScale = buffer.getFloat();
        int flags = buffer.getInt();
        float unkFloat1 = buffer.getFloat();
        float unkFloat2 = buffer.getFloat();

        return new Md6LodInfo(
            numVertices,
            numFaces,
            negBounds,
            posBounds,
            vertexOffset,
            vertexScale,
            uvOffset,
            uvScale,
            flags,
            unkFloat1,
            unkFloat2
        );
    }
}
