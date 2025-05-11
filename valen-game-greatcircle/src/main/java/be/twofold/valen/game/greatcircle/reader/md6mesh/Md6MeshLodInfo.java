package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.util.*;

record Md6MeshLodInfo(
    int numVertices,
    int numFaces,
    Bounds bounds,
    Vector3 vertexOffset,
    float vertexScale,
    Vector2 uvOffset,
    float uvScale,
    int vertexMask,
    float unkFloat2,
    float unkFloat3,
    short[] blendShapeReferences,
    List<Md6MeshBlendShape> blendShapes
) implements LodInfo {
    static Md6MeshLodInfo read(DataSource source) throws IOException {
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

        var numBlendShapes = source.readInt();
        short[] blendShapeReferences;
        List<Md6MeshBlendShape> blendShapes;
        if (numBlendShapes == 0) {
            blendShapeReferences = new short[0];
            blendShapes = List.of();
        } else {
            blendShapeReferences = source.readShorts(source.readInt());
            blendShapes = source.readObjects(numBlendShapes, Md6MeshBlendShape::read);
        }

        return new Md6MeshLodInfo(
            numVertices,
            numFaces,
            bounds,
            vertexOffset,
            vertexScale,
            uvOffset,
            uvScale,
            flags,
            unkFloat1,
            unkFloat2,
            blendShapeReferences,
            blendShapes
        );
    }
}
