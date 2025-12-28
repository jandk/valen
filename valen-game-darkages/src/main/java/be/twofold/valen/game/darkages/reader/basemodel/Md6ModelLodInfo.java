package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.math.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Md6ModelLodInfo(
    int numVertices,
    int numFaces,
    byte influence,
    Bounds bounds,
    Vector3 vertexOffset,
    float vertexScale,
    Vector2 uvOffset,
    float uvScale,
    int vertexMask,
    float unknown2,
    float unknown3,
    short unknown4
) implements LodInfo {
    public static Md6ModelLodInfo read(BinarySource source) throws IOException {
        var numVertices = source.readInt();
        var numFaces = source.readInt();
        var influence = source.readByte();
        var bounds = Bounds.read(source);
        var vertexOffset = Vector3.read(source);
        var vertexScale = source.readFloat();
        var uvOffset = Vector2.read(source);
        var uvScale = source.readFloat();
        var vertexMask = source.readInt();
        var unknown2 = source.readFloat();
        var unknown3 = source.readFloat();
        var unknown4 = source.readShort();

        return new Md6ModelLodInfo(
            numVertices,
            numFaces,
            influence,
            bounds,
            vertexOffset,
            vertexScale,
            uvOffset,
            uvScale,
            vertexMask,
            unknown2,
            unknown3,
            unknown4
        );
    }
}
