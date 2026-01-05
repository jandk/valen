package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.game.idtech.geometry.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

final class BlendShapeReader {
    BlendShapeReader() {
    }

    static Map<Integer, List<BlendShape>> readBlendShapes(
        BinarySource source,
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
        BinarySource source,
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
            var result = new BlendShapeInternalReader(source)
                .read(md6MeshBlendShapes, i, numDeltaIndices, deltaIndices, bufferOffset);
            if (result.indices().length() > 0) {
                blendShapes.add(result);
            }
        }
        return blendShapes;
    }

    private static final class BlendShapeInternalReader {
        private final BinarySource source;
        private int index = 0;
        private int indexIndex = 0;
        private int valueIndex = 0;

        private BlendShapeInternalReader(BinarySource source) {
            this.source = source;
        }

        private BlendShape read(List<Md6MeshBlendShape> shapes, int i, int numIndices, List<BlendShapeDeltaIndex> indices, int bufferOffset) throws IOException {
            var shape = shapes.get(i);
            var start = shape.deltaIndexStart();
            var end = i == shapes.size() - 1 ? numIndices : shapes.get(i + 1).deltaIndexStart();
            var numDisplacements = 0;
            for (var idx = start; idx < end; idx++) {
                var deltaIndex = indices.get(idx);
                var cardinality = Integer.bitCount(deltaIndex.occupied1()) + Integer.bitCount(deltaIndex.occupied2());
                numDisplacements += cardinality;
            }

            var indexBuffer = Shorts.Mutable.allocate(numDisplacements);
            var valueBuffer = Floats.Mutable.allocate(numDisplacements * 3);
            var index = 0;
            for (var idx = start; idx < end; idx++) {
                var deltaIndex = indices.get(idx);
                source.position(bufferOffset + deltaIndex.offset() * 16L);
                index = decode(deltaIndex.occupied1(), source, indexBuffer, valueBuffer, deltaIndex.hasOrientation());
                index = decode(deltaIndex.occupied2(), source, indexBuffer, valueBuffer, deltaIndex.hasOrientation());
            }

            return new BlendShape(shape.name(), valueBuffer, indexBuffer);
        }

        private int decode(int mask, BinarySource source, Shorts.Mutable indexBuffer, Floats.Mutable displacementBuffer, boolean hasOrientation) throws IOException {
            for (var i = 0; i < 32; i++) {
                var set = (mask & 1) != 0;
                mask >>>= 1;

                if (set) {
                    indexBuffer.set(indexIndex++, (short) index);
                    readHalf4(source, displacementBuffer);
                    if (hasOrientation) {
                        source.skip(8);
                    }
                }
                index++;
            }
            return index;
        }

        private void readHalf4(BinarySource source, Floats.Mutable buffer) throws IOException {
            for (var i = 0; i < 3; i++) {
                buffer.set(valueIndex++, Float.float16ToFloat(source.readShort()));
            }
            source.skip(2);
        }
    }

    record BlendShapeDeltaIndex(
        int offset,
        int occupied1,
        int occupied2,
        boolean hasOrientation
    ) {
        static BlendShapeDeltaIndex read(BinarySource source) throws IOException {
            var offset = source.readInt();
            var occupied1 = source.readInt();
            var occupied2 = source.readInt();
            var hasOrientation = source.readBool(BoolFormat.INT);
            return new BlendShapeDeltaIndex(offset, occupied1, occupied2, hasOrientation);
        }
    }
}
