package be.twofold.valen.export.cast.mappers;

import be.twofold.tinycast.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public final class CastModelMapper {
    private final CastSkeletonMapper skeletonMapper = new CastSkeletonMapper();
    private final CastMaterialMapper materialMapper;

    public CastModelMapper(Path castPath, Path imagePath) {
        materialMapper = new CastMaterialMapper(castPath, imagePath);
    }

    public CastNodes.Model map(Model value, CastNodes.Root root) throws IOException {
        var modelNode = root.createModel();
        value.name().ifPresent(modelNode::setName);
        value.skeleton().ifPresent(skeleton -> skeletonMapper.map(skeleton, modelNode));
        for (var mesh : value.meshes()) {
            mapMesh(modelNode, mesh);
        }
        return modelNode;
    }

    private void mapMesh(CastNodes.Model modelNode, Mesh mesh) throws IOException {
        var meshNode = modelNode.createMesh();
        mesh.name().ifPresent(meshNode::setName);
        meshNode.setFaceBuffer(mesh.indices().asBuffer());
        meshNode.setVertexPositionBuffer(mesh.positions().asBuffer());
        mesh.normals().ifPresent(buffer -> meshNode.setVertexNormalBuffer(buffer.asBuffer()));
        mesh.tangents().ifPresent(buffer -> meshNode.setVertexTangentBuffer(mapTangentBuffer(buffer.asBuffer())));

        var colorBuffers = mesh.colors().stream().toList();
        colorBuffers.forEach(buffer -> meshNode.addVertexColorBufferI32(mapColorBuffer(buffer.asBuffer())));
        meshNode.setColorLayerCount(colorBuffers.size());

        var uvBuffers = mesh.texCoords();
        uvBuffers.forEach(buffer -> meshNode.addVertexUVBuffer(buffer.asBuffer()));
        meshNode.setUVLayerCount(uvBuffers.size());

        if (mesh.maxInfluence() != 0) {
            meshNode.setMaximumWeightInfluence(mesh.maxInfluence());
            mesh.joints().ifPresent(shorts -> meshNode.setVertexWeightBoneBuffer(shorts.asBuffer()));
            mesh.weights().ifPresent(floats -> meshNode.setVertexWeightValueBuffer(floats.asBuffer()));
        }

        if (mesh.material().isPresent()) {
            meshNode.setMaterial(materialMapper.map(mesh.material().get(), modelNode));
        }

        buildMorphTargets(modelNode, meshNode, mesh.blendShapes());
    }

    private FloatBuffer mapTangentBuffer(FloatBuffer buffer) {
        buffer.rewind();
        var limit = buffer.limit();
        var newBuffer = FloatBuffer.allocate(limit * 3 / 4);
        for (int i = 0, o = 0; i < limit; i += 4, o += 3) {
            newBuffer.put(o/**/, buffer.get(i/**/));
            newBuffer.put(o + 1, buffer.get(i + 1));
            newBuffer.put(o + 2, buffer.get(i + 2));
        }
        return newBuffer;
    }

    private IntBuffer mapColorBuffer(Buffer buffer) {
        Check.argument(buffer instanceof ByteBuffer, "Unsupported color buffer type");
        return ((ByteBuffer) buffer).asIntBuffer();
    }

    private void buildMorphTargets(CastNodes.Model modelNode, CastNodes.Mesh meshNode, List<BlendShape> blendShapes) {
        if (blendShapes.isEmpty()) {
            return;
        }

        for (var blendShape : blendShapes) {
            var absolute = makeAbsolute(meshNode, blendShape);
            modelNode.createBlendShape()
                .setName(blendShape.name())
                .setBaseShape(meshNode.getHash())
                .setTargetShapeVertexIndices(blendShape.indices())
                .setTargetShapeVertexPositions(absolute);
        }
    }

    private FloatBuffer makeAbsolute(CastNodes.Mesh meshNode, BlendShape blendShape) {
        var positions = meshNode.getVertexPositionBuffer();
        var relatives = blendShape.values();
        var absolutes = FloatBuffer.allocate(relatives.capacity());
        for (int i = 0, o = 0; o < absolutes.capacity(); i++, o += 3) {
            var index = Short.toUnsignedInt(blendShape.indices().get(i));
            absolutes.put(positions.get(index * 3/**/) + relatives.get(o/**/));
            absolutes.put(positions.get(index * 3 + 1) + relatives.get(o + 1));
            absolutes.put(positions.get(index * 3 + 2) + relatives.get(o + 2));
        }
        return absolutes;
    }
}
