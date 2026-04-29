package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

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
    public static VegetationLod read(BinarySource source) throws IOException {
        var unknown0 = source.readInt();
        var numVertices = source.readInt();
        var numIndices = source.readInt();
        var unknown1 = source.readInt();
        var unknown2 = source.readInt();
        var unknown3 = source.readInt();
        var vertexMask = source.readInt();
        var bounds = Bounds.read(source);
        var vertexOffset = Vector3.read(source);
        var vertexScale = source.readFloat();
        var uvOffset = Vector2.read(source);
        var uvScale = source.readFloat();
        var always0 = source.readInt();
        var unkFloat1 = source.readFloat();
        var unkFloat2 = source.readFloat();
        source.expectInt(0x65474556); // VEGe

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
