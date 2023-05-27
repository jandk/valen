package be.twofold.valen.reader.md6;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;
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
        int numEdges = buffer.getInt();
        Vector3 negBounds = buffer.getVector3();
        Vector3 posBounds = buffer.getVector3();
        Vector3 vertexOffset = buffer.getVector3();
        float vertexScale = buffer.getFloat();
        Vector2 uvOffset = buffer.getVector2();
        float uvScale = buffer.getFloat();
        int flags = buffer.getInt();
        float unkFloat1 = buffer.getFloat();
        float unkFloat2 = buffer.getFloat();

        return new Md6LodInfo(
            numVertices,
            numEdges,
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

    @Override
    public int numEdges() {
        return numFaces * 3;
    }
}
