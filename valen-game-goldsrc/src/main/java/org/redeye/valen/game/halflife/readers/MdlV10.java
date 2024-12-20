package org.redeye.valen.game.halflife.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import org.redeye.valen.game.halflife.mdl.v10.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public class MdlV10 implements Reader<Model> {

    @Override
    public Model read(Archive archive, Asset asset, DataSource source) throws IOException {
        var ident = source.readInt();
        if (ident != 0x54534449) {
            throw new IllegalStateException("Mdl read unknown ident 0x" + ident);
        }
        var version = source.readInt();
        if (version != 10) {
            throw new IllegalStateException("Mdl read unknown version 0x" + version);
        }
        var name = source.readString(64).trim();
        var fileSize = source.readInt();
        var eyePos = Vector3.read(source);
        var min = Vector3.read(source);
        var max = Vector3.read(source);
        var bbmin = Vector3.read(source);
        var bbmax = Vector3.read(source);

        var flags = StudioHeaderFlags.fromCode(source.readBytes(4));

        var boneCount = source.readInt();
        var boneOffset = source.readInt();
        var boneControllersCount = source.readInt();
        var boneControllersOffset = source.readInt();
        var hitboxCount = source.readInt();
        var hitboxOffset = source.readInt();
        var sequenceCount = source.readInt();
        var sequenceOffset = source.readInt();
        var sequenceGroupsCount = source.readInt();
        var sequenceGroupsOffset = source.readInt();
        var textureCount = source.readInt();
        var textureOffset = source.readInt();
        var textureDataOffset = source.readInt();
        var skinRefCount = source.readInt();
        var skinFamiliesCount = source.readInt();
        var skinOffset = source.readInt();
        var bodyPartCount = source.readInt();
        var bodyPartOffset = source.readInt();
        var attachmentCount = source.readInt();
        var attachmentOffset = source.readInt();
        var soundCount = source.readInt();
        var soundOffset = source.readInt();
        var soundGroupCount = source.readInt();
        var soundGroupOffset = source.readInt();
        var transitionCount = source.readInt();
        var transitionOffset = source.readInt();

        source.seek(bodyPartOffset);
        var bodyParts = new ArrayList<StudioBodyPart>(bodyPartCount);
        for (long i = 0; i < bodyPartCount; i++) {
            source.seek((long) bodyPartOffset + i * 76);
            bodyParts.add(StudioBodyPart.read(source));
        }

        source.seek(boneOffset);
        var bones = new ArrayList<>(boneCount);
        for (long i = 0; i < boneCount; i++) {
            source.seek((long) boneOffset + i * 112);
            bones.add(StudioBone.read(source));
        }
        var meshes = new ArrayList<Mesh>();

        for (StudioBodyPart studioBodyPart : bodyParts) {
            for (StudioModel studioModel : studioBodyPart.models()) {
                var rawVertices = studioModel.vertices();
                for (StudioMesh studioMesh : studioModel.meshes()) {
                    var faceBuffer = ShortBuffer.allocate(studioMesh.triangleCount() * 3);

                    for (StudioStrip strip : studioMesh.strips()) {
                        if (strip.isFan()) {
                            for (int i = 1; i < strip.tris().size() - 1; i++) {
                                faceBuffer.put((short) strip.tris().get(0).vertexIdx());
                                faceBuffer.put((short) strip.tris().get(i + 1).vertexIdx());
                                faceBuffer.put((short) strip.tris().get(i).vertexIdx());
                            }
                        } else {
                            for (int i = 0; i < strip.tris().size() - 2; i++) {
                                faceBuffer.put((short) strip.tris().get(i).vertexIdx());
                                faceBuffer.put((short) strip.tris().get(i + 2 - (i & 1)).vertexIdx());
                                faceBuffer.put((short) strip.tris().get(i + 1 + (i & 1)).vertexIdx());
                            }
                        }
                    }
                    var normalBuffer = FloatBuffer.allocate(studioMesh.vertexCount() * 3);
                    var vertexBuffer = FloatBuffer.allocate(studioMesh.vertexCount() * 3);
                    var uvBuffer = FloatBuffer.allocate(studioMesh.vertexCount() * 2);
                    for (int i = 0; i < studioMesh.vertexCount(); i++) {
                        vertexBuffer.put(rawVertices[studioMesh.vertexOffset() + i + 0]);
                        vertexBuffer.put(rawVertices[studioMesh.vertexOffset() + i + 1]);
                        vertexBuffer.put(rawVertices[studioMesh.vertexOffset() + i + 2]);
                        normalBuffer.put(0);
                        normalBuffer.put(1);
                        normalBuffer.put(0);
                        uvBuffer.put(1.0f);
                        uvBuffer.put(1.0f);
                    }
                    faceBuffer.flip();
                    vertexBuffer.flip();
                    uvBuffer.flip();
                    var faceInfo = new VertexBuffer.Info<>(null, ElementType.Scalar, ComponentType.UnsignedInt, false);
                    var mesh = new Mesh(
                        studioModel.name(),
                        new VertexBuffer(faceBuffer, faceInfo),
                        Map.of(
                            Semantic.Position, new VertexBuffer(vertexBuffer, VertexBuffer.Info.POSITION),
                            Semantic.Normal, new VertexBuffer(normalBuffer, VertexBuffer.Info.NORMAL),
                            new Semantic.TexCoord(0), new VertexBuffer(uvBuffer, VertexBuffer.Info.texCoords(0))
                        ),
                        new Material(studioModel.name(), List.of())
                    );
                    meshes.add(mesh);
                }
            }
        }

        var model = new Model(name, meshes, null);
        return model;
    }

    @Override
    public boolean canRead(Asset asset) {
        return asset.id().fileName().endsWith(".mdl");
    }
}
