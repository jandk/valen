package org.redeye.valen.game.spacemarines2.converters;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public class MeshConverter {
    private final Map<Integer, Integer> boneMap;

    public MeshConverter(Map<Integer, Integer> boneMap) {
        this.boneMap = boneMap;
    }

    private static String extractMaterialName(ObjSplit split) {
        String materialName;
        String shadingMtlTex = split.materialInfo.get("shadingMtl_Tex").getAsString();
        if (!shadingMtlTex.isBlank() && !shadingMtlTex.isEmpty()) {
            materialName = shadingMtlTex;
        } else {
            materialName = split.materialInfo.getAsJsonObject("layer0").get("texName").getAsString();
        }
        return materialName;
    }

    public static byte[] renormalize(byte[] byteArray) {
        int sum = 0;
        for (byte b : byteArray) {
            sum += Byte.toUnsignedInt(b);
        }
        double scaleFactor = 255.0 / sum;
        byte[] normalizedArray = new byte[byteArray.length];
        double totalRoundedSum = 0;
        for (int i = 0; i < byteArray.length; i++) {
            double scaledValue = (byteArray[i] & 0xFF) * scaleFactor;
            normalizedArray[i] = (byte) Math.round(scaledValue);
            totalRoundedSum += Byte.toUnsignedInt(normalizedArray[i]);
        }

        int difference = (int) totalRoundedSum - 255;
        if (difference != 0) {
            for (int i = 0; i < byteArray.length; i++) {
                if (difference > 0 && (normalizedArray[i] & 0xFF) > 0) {
                    normalizedArray[i]--;  // Reduce a value to decrease sum
                    difference--;
                } else if (difference < 0 && (normalizedArray[i] & 0xFF) < 255) {
                    normalizedArray[i]++;  // Increase a value to increase sum
                    difference++;
                }
                if (difference == 0) break;
            }
        }

        return normalizedArray;
    }

    private static Vector3 readSNorm16Vector3(DataSource source) throws IOException {
        return new Vector3(
            MathF.unpackSNorm16(source.readShort()),
            MathF.unpackSNorm16(source.readShort()),
            MathF.unpackSNorm16(source.readShort())
        );
    }

    private static Geo.Reader<FloatBuffer> readCompressedPosition(VertCompressParams compressParams) {
        return (source, dst) -> readSNorm16Vector3(source).fma(compressParams.scale(), compressParams.offset()).toBuffer(dst);
    }

    private static Geo.Reader<FloatBuffer> readVector3() {
        return (source, dst) -> Vector3.read(source).toBuffer(dst);
    }

    private static Geo.Reader<FloatBuffer> readCompressedS16Normals() {
        return (source, dst) -> decompressNormalFromInt16(source.readShort()).toBuffer(dst);
    }

    private static Geo.Reader<FloatBuffer> readCompressedF32Normals() {
        return (source, dst) -> decompressNormalFromFloat(source.readFloat()).toBuffer(dst);
    }

    private static Vector3 decompressNormalFromInt16(short w) {
        float fracX = (1.0f / 181) * Math.abs(w) % 1.0f;
        float fracZ = (1.0f / (181.0f * 181.0f)) * Math.abs(w) % 1.0f;
        float x = (-1.0f + 2.0f * fracX) * (181.0f / 179.0f);
        float z = (-1.0f + 2.0f * fracZ) * (181.0f / 180.0f);
        float y = (Math.signum(w) * MathF.sqrt(Math.max(0.0f, 1.0f - x * x - z * z)));
        return new Vector3(x, y, z).normalize();
    }

    private static Vector3 decompressNormalFromFloat(float w) {
        var x = -1.f + 2.0f * ((w * 0.00390625f) % 1.f);
        var y = -1.f + 2.0f * ((w * 0.0000152587890625f) % 1.f);
        var z = -1.f + 2.0f * ((w * 0.000000059604644775390625f) % 1.f);
        return new Vector3(x, y, z).normalize();
    }

    private Geo.Reader<ByteBuffer> readByteBoneIndices(Map<Integer, Integer> boneMap) {
        return (source, buffer) -> {
            int i0 = Byte.toUnsignedInt(source.readByte());
            int i1 = Byte.toUnsignedInt(source.readByte());
            int i2 = Byte.toUnsignedInt(source.readByte());
            int i3 = Byte.toUnsignedInt(source.readByte());
            buffer.put(boneMap.get(i0).byteValue());
            buffer.put(boneMap.get(i1).byteValue());
            buffer.put(boneMap.get(i2).byteValue());
            buffer.put(boneMap.get(i3).byteValue());
        };
    }

    private Geo.Reader<ShortBuffer> readShortBoneIndices(Map<Integer, Integer> boneMap) {
        return (source, buffer) -> {
            buffer.put(boneMap.get((int) source.readShort()).shortValue());
            buffer.put(boneMap.get((int) source.readShort()).shortValue());
            buffer.put(boneMap.get((int) source.readShort()).shortValue());
            buffer.put(boneMap.get((int) source.readShort()).shortValue());
        };
    }

    public List<Mesh> extractBySplitInfo(GeometryManager geometryManager, List<Short> lod0Ids, List<ObjSplit> splits, List<ObjGeomStream> streams, ArrayList<Material> materials) throws IOException {
        var objSplitInfo = geometryManager.objSplitInfo;
        var meshes = new ArrayList<Mesh>();
        for (int j = 0; j < objSplitInfo.size(); j++) {
            ObjObj obj = geometryManager.objects.get(j);
            if (!lod0Ids.isEmpty() && !lod0Ids.contains(obj.getId())) {
                continue;
            }
            ObjSplitRange objSplitRange = objSplitInfo.get(j);
            if (objSplitRange.numSplits == 0) {
                continue;
            }
            for (int i = objSplitRange.startIndex; i < objSplitRange.startIndex + objSplitRange.numSplits; i++) {
                convertSplitMesh(splits.get(i), streams, materials)
                    .map(m -> m.withName(obj.getName()))
                    .ifPresent(meshes::add);
            }
        }
        return meshes;
    }

    public Optional<Mesh> convertSplitMesh(ObjSplit split, List<ObjGeomStream> streams, List<Material> materials) throws IOException {
        ObjGeom geom = split.geom;
        if (false) {
            System.out.println(split);
            geom.streams.forEach((slot, stream) -> {
                System.out.printf("Stream(%d) %s: stride: %d, %s, %s, %s%n", streams.indexOf(stream), slot, stream.stride, stream.fvf, stream.flags, geom.flags);
            });
        }
        String materialName = extractMaterialName(split);
        var matObj = materials.stream().filter(material -> materialName.equals(material.name())).findFirst().orElseGet(() -> new Material(materialName, List.of()));
        if (!materials.contains(matObj)) {
            materials.add(new Material(materialName, List.of()));
        }

        var attributes = new HashMap<Semantic, VertexBuffer>();

        for (Map.Entry<GeomStreamSlot, ObjGeomStream> e : geom.streams.entrySet()) {
            GeomStreamSlot slot = e.getKey();
            ObjGeomStream stream = e.getValue();
            if (stream != null && !stream.fvf.isEmpty())
                readStreamV2(stream, geom.streamsOffset.get(slot) + split.startVert * stream.stride, split.texCoordMaxTile, split.nVert, split.vertCompParams, attributes);
        }

        var indicesStreamEntry = geom.streams.entrySet().stream().filter(entry -> entry.getValue() != null && entry.getValue().fvf.isEmpty()).findFirst().orElseThrow();
        ObjGeomStream indicesStream = indicesStreamEntry.getValue();
        if (indicesStream.stride > 6 && indicesStream.stride < 12) {
            System.err.println("Invalid index stream stride: " + indicesStream.stride);
            return Optional.empty();
        }
        ByteBuffer indicesBuffer = ByteBuffer.wrap(indicesStream.data).order(ByteOrder.LITTLE_ENDIAN);
        indicesBuffer.position(split.startFace * indicesStream.stride + (geom.streamsOffset.get(indicesStreamEntry.getKey())));
        short[] indices = new short[split.nFace * 3];
        indicesBuffer.asShortBuffer().get(indices, 0, split.nFace * 3);
        var newIndicesBuffer = ShortBuffer.allocate(split.nFace * 3);
        int max = 0;
        for (short index : indices) {
            int v0 = Short.toUnsignedInt(index) - split.startVert;
            if (v0 > max) {
                max = v0;
            }
            Check.index(v0, 65535);
            newIndicesBuffer.put((short) v0);
        }
        Check.state(max + 1 == split.nVert);
        attributes.values().forEach(vertexBuffer -> vertexBuffer.buffer().rewind());

        return Optional.of(new Mesh(
            null,
            new VertexBuffer(newIndicesBuffer.rewind(), VertexBuffer.Info.faces(ComponentType.UnsignedShort)),
            attributes,
            matObj));
    }

    private void readStreamV2(ObjGeomStream vertexStream, int offset, Map<Integer, Integer> uvTiles, int vertCount, VertCompressParams compressParams, Map<Semantic, VertexBuffer> attributes) throws IOException {
        var source = DataSource.fromArray(vertexStream.data, offset + vertexStream.vBuffOffset, (vertexStream.size - offset));
        Set<FVF> streamFVF = vertexStream.fvf;
        int bufferOffset = 0;
        if (streamFVF.contains(FVF.VERT)) {
            Geo.Accessor<?> accessor;
            if (streamFVF.contains(FVF.VERT_COMPR)) {
                accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.POSITION, readCompressedPosition(compressParams));
                if (streamFVF.contains(FVF.NORM_IN_VERT4)) {
                    bufferOffset += 6;
                } else {
                    bufferOffset += 8;
                }
            } else {
                accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.POSITION, readVector3());
                bufferOffset += 12;
            }
            source.seek(0);
            attributes.put(Semantic.Position, accessor.read(source));
        }

        if (streamFVF.contains(FVF.NORM) && streamFVF.contains(FVF.NORM_IN_VERT4)) {
            Geo.Accessor<?> accessor;
            if (streamFVF.contains(FVF.NORM_COMPR)) {
                accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.NORMAL, readCompressedS16Normals());
                bufferOffset += 2;
            } else {
                accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.NORMAL, readCompressedF32Normals());
                bufferOffset += 4;
            }
            source.seek(0);
            attributes.put(Semantic.Normal, accessor.read(source));
        }

        if (streamFVF.contains(FVF.WEIGHT4)) {
            var accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.weights(0, ComponentType.UnsignedByte), (source1, buffer) -> {
                byte[] weights = new byte[4];
                weights[0] = source1.readByte();
                weights[1] = source1.readByte();
                weights[2] = source1.readByte();
                weights = renormalize(weights);
                source1.readByte();
                buffer.put(weights);
            });
            bufferOffset += 4;
            source.seek(0);
            attributes.put(Semantic.Weights0, accessor.read(source));
        }
        if (streamFVF.contains(FVF.WEIGHT8)) {
            var accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.weights(0, ComponentType.UnsignedByte), (source1, buffer) -> {
                byte[] weights = new byte[4];
                weights[0] = source1.readByte();
                weights[1] = source1.readByte();
                weights[2] = source1.readByte();
                weights[3] = source1.readByte();
                weights = renormalize(weights);
                buffer.put(weights);
            });
            bufferOffset += 4;
            source.seek(0);
            attributes.put(Semantic.Weights0, accessor.read(source));
            accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.weights(1, ComponentType.UnsignedByte), (source1, buffer) -> {
                byte[] weights = new byte[4];
                weights[0] = source1.readByte();
                weights[1] = source1.readByte();
                weights[2] = source1.readByte();
                source1.readByte();
                weights = renormalize(weights);
                buffer.put(weights);
            });
            bufferOffset += 4;
            source.seek(0);
            attributes.put(Semantic.Weights1, accessor.read(source));
        }

        if (streamFVF.contains(FVF.INDICES)) {
            if (!streamFVF.contains(FVF.WEIGHT4) && !streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.joints(0, ComponentType.UnsignedByte), readByteBoneIndices(boneMap));
                bufferOffset += 4;
                source.seek(0);
                attributes.put(Semantic.Joints0, accessor.read(source));
                var weights = new byte[vertCount * 4];
                weights[0] = (byte) 255;
                attributes.put(Semantic.Weights0, new VertexBuffer(ByteBuffer.wrap(weights), VertexBuffer.Info.weights(0, ComponentType.UnsignedByte)));
            }

            if (streamFVF.contains(FVF.WEIGHT4) || streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.joints(0, ComponentType.UnsignedByte), readByteBoneIndices(boneMap));
                bufferOffset += 4;
                source.seek(0);
                attributes.put(Semantic.Joints0, accessor.read(source));
            }
            if (streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.joints(1, ComponentType.UnsignedByte), readByteBoneIndices(boneMap));
                bufferOffset += 4;
                source.seek(0);
                attributes.put(Semantic.Joints1, accessor.read(source));
            }

        }
        source.seek(0);
        if (streamFVF.contains(FVF.INDICES16)) {
            if (!streamFVF.contains(FVF.WEIGHT4) && !streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.joints(0, ComponentType.UnsignedShort), readShortBoneIndices(boneMap));
                bufferOffset += 8;
                source.seek(0);
                attributes.put(Semantic.Joints0, accessor.read(source));

                var weights = new byte[vertCount * 4];
                weights[0] = (byte) 255;
                attributes.put(Semantic.Weights0, new VertexBuffer(ByteBuffer.wrap(weights), VertexBuffer.Info.weights(0, ComponentType.UnsignedByte)));
            }

            if (streamFVF.contains(FVF.WEIGHT4) || streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.joints(0, ComponentType.UnsignedShort), readShortBoneIndices(boneMap));
                bufferOffset += 8;
                source.seek(0);
                attributes.put(Semantic.Joints0, accessor.read(source));
            }
            if (streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.joints(1, ComponentType.UnsignedShort), readShortBoneIndices(boneMap));
                bufferOffset += 8;
                source.seek(0);
                attributes.put(Semantic.Joints1, accessor.read(source));
            }
        }

        for (FVF tangentLayerFlag : EnumSet.range(FVF.TANG0, FVF.TANG4)) {
            if (streamFVF.contains(tangentLayerFlag)) {
                source.seek(0);
                Check.state(streamFVF.contains(FVF.TANG_COMPR));
                if (streamFVF.contains(FVF.TANG_COMPR)) {
                    bufferOffset += 4;
                }
            }
        }

        int colorLayerCount = 0;
        for (FVF colorLayerFlag : EnumSet.range(FVF.COLOR0, FVF.COLOR5)) {
            if (streamFVF.contains(colorLayerFlag)) {
                var accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.colors(colorLayerCount, ComponentType.UnsignedByte), (source1, buffer) -> {
                    var color = source1.readBytes(4);
                    if (color[0] == 0 && color[1] == 0 && color[2] == 0) { // Avoid full black VColors for now
                        color[0] = -1;
                        color[1] = -1;
                        color[2] = -1;
                    }
                    buffer.put(color);
                });
                source.seek(0);
                attributes.put(new Semantic.TexCoord(colorLayerCount), accessor.read(source));
                colorLayerCount++;
                bufferOffset += 4;
            }
        }

        int uvLayerCount = 0;
        int uvLayerId = 0;
        for (FVF uvLayerFlag : EnumSet.range(FVF.TEX0, FVF.TEX5)) {
            if (streamFVF.contains(uvLayerFlag)) {
                float uvTile;
                if (uvTiles.containsKey(uvLayerId) || streamFVF.contains(FVF.values()[FVF.TEX0_COMPR.ordinal() + uvLayerId])) {
                    uvTile = uvTiles.get(uvLayerId);
                } else {
                    uvTile = 1;
                }
                var accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.texCoords(uvLayerCount), (source1, buffer) -> {
                    float u = MathF.unpackSNorm16(source1.readShort());
                    float v = MathF.unpackSNorm16(source1.readShort());

                    if (uvTile > 1) {
                        u *= (uvTile);
                        v *= (uvTile);
                    }

                    buffer.put(u);
                    buffer.put(v);
                });
                source.seek(0);
                attributes.put(new Semantic.TexCoord(uvLayerCount), accessor.read(source));
                uvLayerCount++;
                bufferOffset += 4;
            }
            uvLayerId++;
        }

        if (streamFVF.contains(FVF.NORM) && !streamFVF.contains(FVF.NORM_IN_VERT4)) {
            var accessor = new Geo.Accessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBuffer.Info.NORMAL, readVector3());
            bufferOffset += 12;
            source.seek(0);
            attributes.put(Semantic.Normal, accessor.read(source));
        }

        Check.state(bufferOffset <= vertexStream.stride);
    }

}
