package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;

public record VegetationLod(
    int unknown0,
    int numVertices,
    int numIndices,
    int vertexMask,
    int unknown1,
    int unknown2,
    int unknown3,
    Bounds bounds,
    Vector3 vertexOffset,
    float vertexScale,
    Vector2 uvOffset,
    float uvScale,
    int always0,
    float unkFloat1,
    float unkFloat2
) implements LodInfo {
    public static VegetationLod read(BinaryReader reader) throws IOException {
        var unknown0 = reader.readInt();
        var numVertices = reader.readInt();
        var numIndices = reader.readInt();
        var unknown1 = reader.readInt();
        var unknown2 = reader.readInt();
        var unknown3 = reader.readInt();
        var vertexMask = reader.readInt();
        var bounds = Bounds.read(reader);
        var vertexOffset = Vector3.read(reader);
        var vertexScale = reader.readFloat();
        var uvOffset = Vector2.read(reader);
        var uvScale = reader.readFloat();
        var always0 = reader.readInt();
        var unkFloat1 = reader.readFloat();
        var unkFloat2 = reader.readFloat();
        reader.expectInt(0x65474556); // VEGe

        return new VegetationLod(
            unknown0,
            numVertices,
            numIndices,
            vertexMask,
            unknown1,
            unknown2,
            unknown3,
            bounds,
            vertexOffset,
            vertexScale,
            uvOffset,
            uvScale,
            always0,
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
