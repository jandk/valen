package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.idtech.geometry.*;

import java.io.*;
import java.nio.*;
import java.util.*;

final class BlendShapeReader {
    BlendShapeReader() {
    }

    static Map<Integer, List<BlendShape>> readBlendShapes(
        BinaryReader reader,
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
            var shapes = readBlendShapesForSingleMesh(reader, shapeInfos, layout.deltaIndexesBufferOffset(), indexBufferLength, layout.deltaBufferOffset());
            result.put(layout.meshIndex(), shapes);
        }
        return result;
    }

    private static List<BlendShape> readBlendShapesForSingleMesh(
        BinaryReader reader,
        List<Md6MeshBlendShape> md6MeshBlendShapes,
        int indexOffset,
        int indexLength,
        int bufferOffset
    ) throws IOException {
        var numDeltaIndices = indexLength / 16;
        reader.position(indexOffset);
        var deltaIndices = reader.readObjects(numDeltaIndices, BlendShapeDeltaIndex::read);

        var blendShapes = new ArrayList<BlendShape>(md6MeshBlendShapes.size());
        for (var i = 0; i < md6MeshBlendShapes.size(); i++) {
            var result = readBlendShape(reader, md6MeshBlendShapes, i, numDeltaIndices, deltaIndices, bufferOffset);
            if (result.indices().capacity() > 0) {
                blendShapes.add(result);
            }
        }
        return blendShapes;
    }

    private static BlendShape readBlendShape(BinaryReader reader, List<Md6MeshBlendShape> shapes, int i, int numIndices, List<BlendShapeDeltaIndex> indices, int bufferOffset) throws IOException {
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
            reader.position(bufferOffset + deltaIndex.offset() * 16L);
            index = decode(deltaIndex.occupied1(), reader, indexBuffer, index, valueBuffer, deltaIndex.hasOrientation());
            index = decode(deltaIndex.occupied2(), reader, indexBuffer, index, valueBuffer, deltaIndex.hasOrientation());
        }

        return new BlendShape(shape.name(), valueBuffer.flip(), indexBuffer.flip());
    }

    private static int decode(int mask, BinaryReader reader, ShortBuffer indexBuffer, int index, FloatBuffer displacementBuffer, boolean hasOrientation) throws IOException {
        for (var i = 0; i < 32; i++) {
            var set = (mask & 1) != 0;
            mask >>>= 1;

            if (set) {
                indexBuffer.put((short) index);
                readHalf4(reader, displacementBuffer);
                if (hasOrientation) {
                    reader.skip(8);
                }
            }
            index++;
        }
        return index;
    }

    private static void readHalf4(BinaryReader reader, FloatBuffer buffer) throws IOException {
        for (var i = 0; i < 3; i++) {
            buffer.put(Float.float16ToFloat(reader.readShort()));
        }
        reader.skip(2);
    }

    record BlendShapeDeltaIndex(
        int offset,
        int occupied1,
        int occupied2,
        boolean hasOrientation
    ) {
        static BlendShapeDeltaIndex read(BinaryReader reader) throws IOException {
            var offset = reader.readInt();
            var occupied1 = reader.readInt();
            var occupied2 = reader.readInt();
            var hasOrientation = reader.readBoolInt();
            return new BlendShapeDeltaIndex(offset, occupied1, occupied2, hasOrientation);
        }
    }
}
