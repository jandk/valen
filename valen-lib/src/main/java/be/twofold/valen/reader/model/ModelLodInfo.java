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
        var numVertices = buffer.getInt();
        var numEdges = buffer.getInt();
        var flags = buffer.getInt();
        var negBounds = Vector3.read(buffer);
        var posBounds = Vector3.read(buffer);
        var vertexOffset = Vector3.read(buffer);
        var vertexScale = buffer.getFloat();
        var uvOffset = Vector2.read(buffer);
        var uvScale = buffer.getFloat();
        buffer.expectInt(0);
        var unkFloat1 = buffer.getFloat();
        var unkFloat2 = buffer.getFloat();
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
