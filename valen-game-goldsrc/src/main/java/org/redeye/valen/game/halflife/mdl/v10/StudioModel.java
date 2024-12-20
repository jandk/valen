package org.redeye.valen.game.halflife.mdl.v10;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record StudioModel(
    String name,
    int type,
    float boundingRadius,
    List<StudioMesh> meshes,
    byte[] boneVertexInfo,
    byte[] boneNormalInfo,
    float[] vertices,
    float[] normals
) {
    public static StudioModel read(DataSource source) throws IOException {
        var name = source.readString(64).trim();
        var type = source.readInt();
        var boundingRadius = source.readFloat();
        var meshCount = source.readInt();
        var meshOffset = source.readInt();
        var vertexCount = source.readInt();
        var vertexInfoOffset = source.readInt();
        var vertexOffset = source.readInt();
        var normalCount = source.readInt();
        var normalInfoOffset = source.readInt();
        var normalOffset = source.readInt();
        var groupCount = source.readInt();
        var groupOffset = source.readInt();

        var meshes = new ArrayList<StudioMesh>();
        for (long i = 0; i < meshCount; i++) {
            source.seek(meshOffset + i * 20);
            meshes.add(StudioMesh.read(source));
        }
        source.seek(vertexInfoOffset);
        byte[] vertexInfo = source.readBytes(vertexCount);
        source.seek(normalInfoOffset);
        byte[] normalInfo = source.readBytes(vertexCount);

        source.seek(vertexOffset);
        float[] vertices = source.readFloats(vertexCount * 3);
        source.seek(normalOffset);
        float[] normals = source.readFloats(normalCount * 3);

        return new StudioModel(name, type, boundingRadius, meshes, vertexInfo, normalInfo, vertices, normals);
    }

}
