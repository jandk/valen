package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;

public record StaticModelLodInfo(
    int numVertices,
    int numEdges,
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
    public static StaticModelLodInfo read(BinaryReader reader) throws IOException {
        reader.expectInt(-2);
        var numVertices = reader.readInt();
        var numEdges = reader.readInt();
        var flags = reader.readInt();
        var negBounds = Vector3.read(reader);
        var posBounds = Vector3.read(reader);
        var vertexOffset = Vector3.read(reader);
        var vertexScale = reader.readFloat();
        var uvOffset = Vector2.read(reader);
        var uvScale = reader.readFloat();
        reader.expectInt(0);
        var unkFloat1 = reader.readFloat();
        var unkFloat2 = reader.readFloat();
        reader.expectInt(0x724c4d42);

        return new StaticModelLodInfo(
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
