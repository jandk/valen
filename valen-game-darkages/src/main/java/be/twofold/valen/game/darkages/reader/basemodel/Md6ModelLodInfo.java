package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.core.io.BinaryReader;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.idtech.geometry.*;

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
    public static Md6ModelLodInfo read(BinaryReader reader) throws IOException {
        var numVertices = reader.readInt();
        var numFaces = reader.readInt();
        var influence = reader.readByte();
        var bounds = Bounds.read(reader);
        var vertexOffset = Vector3.read(reader);
        var vertexScale = reader.readFloat();
        var uvOffset = Vector2.read(reader);
        var uvScale = reader.readFloat();
        var vertexMask = reader.readInt();
        var unknown2 = reader.readFloat();
        var unknown3 = reader.readFloat();
        var unknown4 = reader.readShort();

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
