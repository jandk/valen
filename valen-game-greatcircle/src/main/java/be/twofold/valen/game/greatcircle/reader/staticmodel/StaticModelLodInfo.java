package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;

public record StaticModelLodInfo(
    int numVertices,
    int numIndices,
    int numSomething,
    int vertexMask,
    Vector3 negBounds,
    Vector3 posBounds,
    Vector3 vertexOffset,
    float vertexScale,
    Vector2 uvOffset,
    float uvScale,
    float unkFloat2,
    float unkFloat3
) implements LodInfo {
    public static StaticModelLodInfo read(BinarySource source) throws IOException {
        int mode = source.readInt();
        var numVertices = source.readInt();
        var numIndices = source.readInt();
        var numSomething = mode == -3 ? source.readInt() : 0;
        var vertexMask = source.readInt();
        var negBounds = Vector3.read(source);
        var posBounds = Vector3.read(source);
        var vertexOffset = Vector3.read(source);
        var vertexScale = source.readFloat();
        var uvOffset = Vector2.read(source);
        var uvScale = source.readFloat();
        source.expectInt(0);
        var unkFloat1 = source.readFloat();
        var unkFloat2 = source.readFloat();
        source.expectInt(0x724C4D42);

        return new StaticModelLodInfo(
            numVertices,
            numIndices,
            numSomething,
            vertexMask,
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
        assert numIndices % 3 == 0;
        return numIndices / 3;
    }
}
