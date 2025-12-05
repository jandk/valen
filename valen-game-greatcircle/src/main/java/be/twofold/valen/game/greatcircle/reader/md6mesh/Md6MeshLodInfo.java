package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;
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
    Shorts blendShapeReferences,
    List<Md6MeshBlendShape> blendShapes
) implements LodInfo {
    static Md6MeshLodInfo read(BinaryReader reader) throws IOException {
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

        var numBlendShapes = reader.readInt();
        var blendShapeReferences = Shorts.empty();
        var blendShapes = List.<Md6MeshBlendShape>of();
        if (numBlendShapes != 0) {
            blendShapeReferences = reader.readShorts(reader.readInt());
            blendShapes = reader.readObjects(numBlendShapes, Md6MeshBlendShape::read);
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
