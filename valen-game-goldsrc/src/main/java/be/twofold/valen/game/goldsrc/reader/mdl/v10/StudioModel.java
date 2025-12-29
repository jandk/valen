package be.twofold.valen.game.goldsrc.reader.mdl.v10;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record StudioModel(
    String name,
    int type,
    float boundingRadius,
    List<StudioMesh> meshes,
    Bytes boneVertexInfo,
    Bytes boneNormalInfo,
    Floats vertices,
    Floats normals
) {
    public static StudioModel read(BinarySource source) throws IOException {
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
            source.position(meshOffset + i * 20);
            meshes.add(StudioMesh.read(source));
        }
        source.position(vertexInfoOffset);
        Bytes vertexInfo = source.readBytes(vertexCount);
        source.position(normalInfoOffset);
        Bytes normalInfo = source.readBytes(vertexCount);

        source.position(vertexOffset);
        Floats vertices = source.readFloats(vertexCount * 3);
        source.position(normalOffset);
        Floats normals = source.readFloats(normalCount * 3);

        return new StudioModel(name, type, boundingRadius, meshes, vertexInfo, normalInfo, vertices, normals);
    }

}
