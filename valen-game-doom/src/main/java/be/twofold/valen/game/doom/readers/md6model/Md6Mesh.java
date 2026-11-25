package be.twofold.valen.game.doom.readers.md6model;

import be.twofold.valen.game.doom.readers.*;
import be.twofold.valen.game.doom.readers.common.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.util.*;

public record Md6Mesh(
    String name,
    String material,
    boolean allowSkinRemapping,
    int numTexCoordSets,
    Triangles triangles,
    int jointOffset,
    int numJoints,
    int checksum,
    boolean hasMorphNormals,
    Optional<Bytes> morphMap,
    int numBlendShapes,
    Optional<Bytes> blendShapeData
) {
    public static Md6Mesh read(BinaryReader reader) throws IOException {
        var name = IdStrings.read(reader);
        var material = IdStrings.read(reader);
        var allowSkinRemapping = reader.readBool(BoolFormat.BYTE);
        var numTexCoordSets = reader.readInt();

        // Triangles
        var numVerts = reader.readInt();
        var numTris = reader.readInt();
        var bounds = Bounds.read(reader);
        var numIndices = numTris * 3;
        var buffer = reader.readBytes(numVerts * Triangles.VERTEX_SIZE + numIndices * Short.BYTES);
        var triangles = Triangles.of(numVerts, numIndices, buffer, bounds);

        var jointOffset = reader.readInt();
        var numJoints = reader.readInt();
        var checksum = reader.readInt();

        // morphMap
        boolean hasMorphMap = reader.readBool(BoolFormat.BYTE);
        boolean hasMorphNormals = hasMorphMap && reader.readBool(BoolFormat.BYTE);
        Bytes morphMap = hasMorphMap ? reader.readBytes(numVerts * (hasMorphNormals ? 8 : 4)) : null;

        // blendShapes
        int numBlendShapes = reader.readInt();
        Bytes blendShapeData = numBlendShapes > 0 ? reader.readBytes(numVerts * numBlendShapes * 8) : null;

        return new Md6Mesh(
            name,
            material,
            allowSkinRemapping,
            numTexCoordSets,
            triangles,
            jointOffset,
            numJoints,
            checksum,
            hasMorphNormals,
            Optional.ofNullable(morphMap),
            numBlendShapes,
            Optional.ofNullable(blendShapeData)
        );
    }
}
