package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.core.io.BinaryReader;
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
    public static Md6ModelLodInfo read(BinaryReader reader) throws IOException {
        var numVertices = reader.readInt();
        var numFaces = reader.readInt();
        var bounds = Bounds.read(reader);
        var vertexOffset = Vector3.read(reader);
        var vertexScale = reader.readFloat();
        var uvOffset = Vector2.read(reader);
        var uvScale = reader.readFloat();
        var flags = reader.readInt();
        var unkFloat1 = reader.readFloat();
        var unkFloat2 = reader.readFloat();

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
