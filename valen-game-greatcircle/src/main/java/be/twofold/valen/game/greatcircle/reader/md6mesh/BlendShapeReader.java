package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.greatcircle.reader.geometry.*;

import java.io.*;
import java.nio.*;
import java.util.*;

final class BlendShapeReader {
    BlendShapeReader() {
    }

    static Map<Integer, List<BlendShape>> readBlendShapes(
        DataSource source,
        List<LodInfo> lodInfos,
        List<GeometryMemoryLayout> memoryLayouts
    ) throws IOException {
        var memoryLayout = memoryLayouts.getFirst();
        var result = new HashMap<Integer, List<BlendShape>>();
        List<GeometryBlendShapeLayout> blendShapeLayouts = memoryLayout.blendShapeLayouts();
        for (int i = 0; i < blendShapeLayouts.size(); i++) {
            var layout = blendShapeLayouts.get(i);
            var indexBufferLength = (layout == memoryLayout.blendShapeLayouts().getLast()
                ? memoryLayout.blendShapeLayouts().getFirst().deltaBufferOffset()
                : blendShapeLayouts.get(i + 1).deltaIndexesBufferOffset()) - layout.deltaIndexesBufferOffset();

            var shapeInfos = ((Md6MeshLodInfo) lodInfos.get(layout.meshIndex())).blendShapes();
            var shapes = readBlendShapesForSingleMesh(source, shapeInfos, layout.deltaIndexesBufferOffset(), indexBufferLength, layout.deltaBufferOffset());
            result.put(layout.meshIndex(), shapes);
        }
        return result;
    }

    private static List<BlendShape> readBlendShapesForSingleMesh(
        DataSource source,
        List<Md6MeshBlendShape> md6MeshBlendShapes,
        int indexOffset,
        int indexLength,
        int bufferOffset
    ) throws IOException {
        var numDeltaIndices = indexLength / 16;
        source.position(indexOffset);
        var deltaIndices = source.readObjects(numDeltaIndices, BlendShapeDeltaIndex::read);

        var blendShapes = new ArrayList<BlendShape>(md6MeshBlendShapes.size());
        for (var i = 0; i < md6MeshBlendShapes.size(); i++) {
            var result = readBlendShape(source, md6MeshBlendShapes, i, numDeltaIndices, deltaIndices, bufferOffset);
            if (result.indices().capacity() > 0) {
                blendShapes.add(result);
            }
        }
        return blendShapes;
    }

    private static BlendShape readBlendShape(DataSource source, List<Md6MeshBlendShape> shapes, int i, int numIndices, List<BlendShapeDeltaIndex> indices, int bufferOffset) throws IOException {
        var shape = shapes.get(i);
        var start = shape.deltaIndexStart();
        var end = i == shapes.size() - 1 ? numIndices : shapes.get(i + 1).deltaIndexStart();
        var numDisplacements = 0;
        for (var idx = start; idx < end; idx++) {
            var deltaIndex = indices.get(idx);
            var cardinality = Integer.bitCount(deltaIndex.occupied1()) + Integer.bitCount(deltaIndex.occupied2());
            numDisplacements += cardinality;
        }

        var indexBuffer = ShortBuffer.allocate(numDisplacements);
        var valueBuffer = FloatBuffer.allocate(numDisplacements * 3);
        var index = 0;
        for (var idx = start; idx < end; idx++) {
            var deltaIndex = indices.get(idx);
            source.position(bufferOffset + deltaIndex.offset() * 16L);
            index = decode(deltaIndex.occupied1(), source, indexBuffer, index, valueBuffer, deltaIndex.hasOrientation());
            index = decode(deltaIndex.occupied2(), source, indexBuffer, index, valueBuffer, deltaIndex.hasOrientation());
        }

        return new BlendShape(shape.name(), valueBuffer.flip(), indexBuffer.flip());
    }

    private static int decode(int mask, DataSource source, ShortBuffer indexBuffer, int index, FloatBuffer displacementBuffer, boolean hasOrientation) throws IOException {
        for (var i = 0; i < 32; i++) {
            var set = (mask & 1) != 0;
            mask >>>= 1;

            if (set) {
                indexBuffer.put((short) index);
                readHalf4(source, displacementBuffer);
                if (hasOrientation) {
                    source.skip(8);
                }
            }
            index++;
        }
        return index;
    }

    private static void readHalf4(DataSource source, FloatBuffer buffer) throws IOException {
        for (var i = 0; i < 3; i++) {
            buffer.put(Float.float16ToFloat(source.readShort()));
        }
        source.skip(2);
    }

    record BlendShapeDeltaIndex(
        int offset,
        int occupied1,
        int occupied2,
        boolean hasOrientation
    ) {
        static BlendShapeDeltaIndex read(DataSource source) throws IOException {
            var offset = source.readInt();
            var occupied1 = source.readInt();
            var occupied2 = source.readInt();
            var hasOrientation = source.readBoolInt();
            return new BlendShapeDeltaIndex(offset, occupied1, occupied2, hasOrientation);
        }
    }
}
