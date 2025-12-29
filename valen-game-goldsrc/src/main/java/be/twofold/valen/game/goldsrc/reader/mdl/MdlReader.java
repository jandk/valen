package be.twofold.valen.game.goldsrc.reader.mdl;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.game.goldsrc.*;
import be.twofold.valen.game.goldsrc.reader.mdl.v10.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class MdlReader implements AssetReader<Model, GoldSrcAsset> {
    @Override
    public boolean canRead(GoldSrcAsset asset) {
        return asset.id().fileName().endsWith(".mdl");
    }

    @Override
    public Model read(BinarySource source, GoldSrcAsset asset) throws IOException {
        var header = MdlHeader.read(source);

        source.position(header.bodyPartOffset());
        var bodyParts = new ArrayList<StudioBodyPart>();
        for (int i = 0; i < header.bodyPartCount(); i++) {
            source.position(header.bodyPartOffset() + i * 76L);
            bodyParts.add(StudioBodyPart.read(source));
        }

        source.position(header.boneOffset());
        var bones = new ArrayList<>();
        for (int i = 0; i < header.boneCount(); i++) {
            source.position(header.boneOffset() + i * 112L);
            bones.add(StudioBone.read(source));
        }
        var meshes = new ArrayList<Mesh>();

        for (StudioBodyPart studioBodyPart : bodyParts) {
            for (StudioModel studioModel : studioBodyPart.models()) {
                var rawVertices = studioModel.vertices();
                for (StudioMesh studioMesh : studioModel.meshes()) {
                    var faceBuffer = Ints.Mutable.allocate(studioMesh.triangleCount() * 3);
                    var faceIndex = 0;

                    for (StudioStrip strip : studioMesh.strips()) {
                        if (strip.isFan()) {
                            for (int i = 1; i < strip.tris().size() - 1; i++) {
                                faceBuffer.set(faceIndex++, strip.tris().get(0).vertIndex());
                                faceBuffer.set(faceIndex++, strip.tris().get(i + 1).vertIndex());
                                faceBuffer.set(faceIndex++, strip.tris().get(i).vertIndex());
                            }
                        } else {
                            for (int i = 0; i < strip.tris().size() - 2; i++) {
                                faceBuffer.set(faceIndex++, strip.tris().get(i).vertIndex());
                                faceBuffer.set(faceIndex++, strip.tris().get(i + 2 - (i & 1)).vertIndex());
                                faceBuffer.set(faceIndex++, strip.tris().get(i + 1 + (i & 1)).vertIndex());
                            }
                        }
                    }
                    var vertexBuffer = Floats.Mutable.allocate(studioMesh.vertexCount() * 3);
                    var vertexIndex = 0;

                    for (int i = 0; i < studioMesh.vertexCount(); i++) {
                        vertexBuffer.set(vertexIndex++, rawVertices.get(studioMesh.vertexOffset() + i + 0));
                        vertexBuffer.set(vertexIndex++, rawVertices.get(studioMesh.vertexOffset() + i + 1));
                        vertexBuffer.set(vertexIndex++, rawVertices.get(studioMesh.vertexOffset() + i + 2));
                    }

                    meshes.add(new Mesh(
                        faceBuffer, vertexBuffer,
                        Optional.empty(), Optional.empty(),
                        List.of(), List.of(),
                        Optional.empty(), Optional.empty(),
                        0, Map.of())
                        .withMaterial(Optional.of(new Material(studioModel.name(), List.of())))
                    );
                }
            }
        }
        return new Model(meshes, Axis.Z);
    }
}
