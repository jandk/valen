package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;

public record Md6ModelLodInfo(
    int numVertices,
    int numFaces,
    Bounds bounds,
    Vector3 vertexOffset,
    float vertexScale,
    Vector2 uvOffset,
    float uvScale,
    int vertexMask,
    float unkFloat2,
    float unkFloat3
) implements LodInfo {
    public static Md6ModelLodInfo read(BinarySource source) throws IOException {
        var numVertices = source.readInt();
        var numFaces = source.readInt();
        var bounds = Bounds.read(source);
        var vertexOffset = Vector3.read(source);
        var vertexScale = source.readFloat();
        var uvOffset = Vector2.read(source);
        var uvScale = source.readFloat();
        var flags = source.readInt();
        var unkFloat1 = source.readFloat();
        var unkFloat2 = source.readFloat();

        return new Md6ModelLodInfo(
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
