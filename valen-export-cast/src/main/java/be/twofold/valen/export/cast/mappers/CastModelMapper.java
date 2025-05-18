package be.twofold.valen.export.cast.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.format.cast.*;

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

    public CastNode.Model map(Model value, CastNode.Root root) throws IOException {
        var modelNode = root.createModel();
        value.name().ifPresent(modelNode::setName);
        value.skeleton().ifPresent(skeleton -> skeletonMapper.map(skeleton, modelNode));
        for (var mesh : value.meshes()) {
            mapMesh(modelNode, mesh);
        }
        return modelNode;
    }

    private void mapMesh(CastNode.Model modelNode, Mesh mesh) throws IOException {
        var meshNode = modelNode.createMesh();
        mesh.name().ifPresent(meshNode::setName);
        meshNode.setFaceBuffer(mesh.indexBuffer().buffer());
        var positionBuffer = (FloatBuffer) mesh.getBuffer(Semantic.POSITION).orElseThrow().buffer();
        meshNode.setVertexPositionBuffer(positionBuffer);
        mesh.getBuffer(Semantic.NORMAL).ifPresent(buffer -> meshNode.setVertexNormalBuffer((FloatBuffer) buffer.buffer()));
        mesh.getBuffer(Semantic.TANGENT).ifPresent(buffer -> meshNode.setVertexTangentBuffer(mapTangentBuffer((FloatBuffer) buffer.buffer())));

        var colorBuffers = mesh.getBuffers(Semantic.COLOR);
        colorBuffers.forEach(buffer -> meshNode.addVertexColorBuffer(mapColorBuffer(buffer.buffer())));
        meshNode.setColorLayerCount(colorBuffers.size());

        var uvBuffers = mesh.getBuffers(Semantic.TEX_COORD);
        uvBuffers.forEach(buffer -> meshNode.addVertexUVBuffer((FloatBuffer) buffer.buffer()));
        meshNode.setUVLayerCount(uvBuffers.size());

        //mesh.getBuffer(Semantic.JOINTS0).ifPresent(joints -> mesh
        //    .getBuffer(Semantic.WEIGHTS0).ifPresent(weights -> {
        //        var numVertices = mesh.getBuffer(Semantic.POSITION).orElseThrow().buffer().capacity() / 3;
        //        mapJointsAndWeights(joints.buffer(), weights.buffer(), meshNode, numVertices);
        //    }));


        if (mesh.material().isPresent()) {
            meshNode.setMaterial(materialMapper.map(mesh.material().get(), modelNode));
        }

        buildMorphTargets(modelNode, meshNode, mesh.blendShapes());
    }

    private FloatBuffer mapTangentBuffer(FloatBuffer buffer) {
        buffer.rewind();
        var limit = buffer.limit();
        for (int i = 0, o = 0; i < limit; i += 4, o += 3) {
            buffer.put(o/**/, buffer.get(i/**/));
            buffer.put(o + 1, buffer.get(i + 1));
            buffer.put(o + 2, buffer.get(i + 2));
        }
        buffer.limit(limit * 3 / 4);
        return buffer;
    }

    private IntBuffer mapColorBuffer(Buffer buffer) {
        if (!(buffer instanceof ByteBuffer bb)) {
            throw new IllegalArgumentException("Unsupported color buffer type: " + buffer.getClass());
        }
        return bb.asIntBuffer();
    }

    private void mapJointsAndWeights(Buffer joints, Buffer weights, CastNode.Mesh meshNode, int numVertices) {
        if (joints.capacity() != weights.capacity()) {
            throw new IllegalStateException("Joints and weights buffers must have the same capacity");
        }
        meshNode.setMaximumWeightInfluence(joints.capacity() / numVertices);

        if (!(weights instanceof ByteBuffer bb)) {
            throw new IllegalStateException("Weights buffer must be a ByteBuffer");
        }

        var weightValues = FloatBuffer.allocate(bb.capacity());
        for (var i = 0; i < bb.capacity(); i++) {
            weightValues.put(Byte.toUnsignedInt(bb.get(i)) * (1.0f / 255.0f));
        }
        weightValues.rewind();

        meshNode
            .setVertexWeightBoneBuffer(joints)
            .setVertexWeightValueBuffer(weightValues);
    }

    private void buildMorphTargets(CastNode.Model modelNode, CastNode.Mesh meshNode, List<BlendShape> blendShapes) throws IOException {
        if (blendShapes.isEmpty()) {
            return;
        }

        for (var blendShape : blendShapes) {
            var absolute = makeAbsolute(meshNode, blendShape);
            modelNode.createBlendShape()
                .setName(blendShape.name())
                .setBaseShape(meshNode.hash())
                .setTargetShapeVertexIndices(blendShape.indices())
                .setTargetShapeVertexPositions(absolute);
        }
    }

    private FloatBuffer makeAbsolute(CastNode.Mesh meshNode, BlendShape blendShape) {
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
