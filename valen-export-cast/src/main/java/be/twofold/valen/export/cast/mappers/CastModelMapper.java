package be.twofold.valen.export.cast.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
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
        meshNode.setFaceBuffer(mesh.indexBuffer().asBuffer());
        meshNode.setVertexPositionBuffer(mesh.getPositions().asBuffer());
        mesh.getNormals().ifPresent(buffer -> meshNode.setVertexNormalBuffer(buffer.asBuffer()));
        mesh.getTangents().ifPresent(buffer -> meshNode.setVertexTangentBuffer(mapTangentBuffer(buffer.asBuffer())));

        var colorBuffers = mesh.getBuffers(Semantic.COLOR);
        colorBuffers.forEach(buffer -> meshNode.addVertexColorBuffer(mapColorBuffer(buffer.buffer().asBuffer())));
        meshNode.setColorLayerCount(colorBuffers.size());

        var uvBuffers = mesh.getTexCoords();
        uvBuffers.forEach(buffer -> meshNode.addVertexUVBuffer(buffer.asBuffer()));
        meshNode.setUVLayerCount(uvBuffers.size());

        mesh.getJoints().ifPresent(buffer -> {
            meshNode.setMaximumWeightInfluence(buffer.info().size());
            meshNode.setVertexWeightBoneBuffer(buffer.buffer().asBuffer());
        });
        mesh.getWeights().ifPresent(buffer -> {
            meshNode.setVertexWeightValueBuffer(buffer.buffer().asBuffer());
        });

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
        Check.argument(buffer instanceof ByteBuffer, "Unsupported color buffer type");
        return ((ByteBuffer) buffer).asIntBuffer();
    }

    private void buildMorphTargets(CastNode.Model modelNode, CastNode.Mesh meshNode, List<BlendShape> blendShapes) {
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
