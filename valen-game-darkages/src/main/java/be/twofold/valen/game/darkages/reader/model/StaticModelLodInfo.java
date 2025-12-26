package be.twofold.valen.game.darkages.reader.model;

import wtf.reversed.toolbox.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;

public record StaticModelLodInfo(
    int numVertices,
    int numEdges,
    int unknown1,
    int unknown2,
    int unknown3,
    int vertexMask,
    Vector3 negBounds,
    Vector3 posBounds,
    Vector3 vertexOffset,
    float vertexScale,
    Vector2 uvOffset,
    float uvScale,
    float unkFloat1,
    float unkFloat2
) implements LodInfo {
    public static StaticModelLodInfo read(BinarySource source) throws IOException {
        source.expectInt(-5);
        var numVertices = source.readInt();
        var numEdges = source.readInt();
        var unknown1 = source.readInt();
        var unknown2 = source.readInt();
        var unknown3 = source.readInt();
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
        source.expectInt(0x724c4d42);

        return new StaticModelLodInfo(
            numVertices,
            numEdges,
            unknown1,
            unknown2,
            unknown3,
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
        assert numEdges % 3 == 0;
        return numEdges / 3;
    }
}
