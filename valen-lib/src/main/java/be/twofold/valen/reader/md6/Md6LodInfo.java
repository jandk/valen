package be.twofold.valen.reader.md6;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.geometry.*;

public record Md6LodInfo(
    int numVertices,
    int numFaces,
    Bounds bounds,
    Vector3 vertexOffset,
    float vertexScale,
    Vector2 uvOffset,
    float uvScale,
    int flags,
    float unkFloat2,
    float unkFloat3
) implements LodInfo {
    public static Md6LodInfo read(BetterBuffer buffer) {
        var numVertices = buffer.getInt();
        var numFaces = buffer.getInt();
        var bounds = Bounds.read(buffer);
        var vertexOffset = Vector3.read(buffer);
        var vertexScale = buffer.getFloat();
        var uvOffset = Vector2.read(buffer);
        var uvScale = buffer.getFloat();
        var flags = buffer.getInt();
        var unkFloat1 = buffer.getFloat();
        var unkFloat2 = buffer.getFloat();

        return new Md6LodInfo(
            numVertices,
            numFaces,
            bounds,
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
