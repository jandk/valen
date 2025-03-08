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

    private static GeoReader<FloatBuffer> readCompressedPosition(VertCompressParams compressParams) {
        return (source, dst) -> readSNorm16Vector3(source).fma(compressParams.scale(), compressParams.offset()).toBuffer(dst);
    }

    private static GeoReader<FloatBuffer> readVector3() {
        return (source, dst) -> Vector3.read(source).toBuffer(dst);
    }

    private static GeoReader<FloatBuffer> readCompressedS16Normals() {
        return (source, dst) -> decompressNormalFromInt16(source.readShort()).toBuffer(dst);
    }

    private static GeoReader<FloatBuffer> readCompressedF32Normals() {
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

    private GeoReader<ByteBuffer> readByteBoneIndices(Map<Integer, Integer> boneMap) {
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

    private GeoReader<ShortBuffer> readShortBoneIndices(Map<Integer, Integer> boneMap) {
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
                    .map(m -> m.withName(Optional.ofNullable(obj.getName())))
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

        var attributes = new ArrayList<VertexBuffer<?>>();

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
        attributes.forEach(vertexBuffer -> vertexBuffer.buffer().rewind());

        return Optional.of(new Mesh(
            new VertexBuffer(newIndicesBuffer.rewind(), VertexBufferInfo.indices(ComponentType.UNSIGNED_SHORT)),
            attributes,
            Optional.of(matObj),
            Optional.empty()
        ));
    }

    private void readStreamV2(ObjGeomStream vertexStream, int offset, Map<Integer, Integer> uvTiles, int vertCount, VertCompressParams compressParams, List<VertexBuffer<?>> attributes) throws IOException {
        var source = DataSource.fromArray(vertexStream.data, offset + vertexStream.vBuffOffset, (vertexStream.size - offset));
        Set<FVF> streamFVF = vertexStream.fvf;
        int bufferOffset = 0;
        if (streamFVF.contains(FVF.VERT)) {
            GeoAccessor<?> accessor;
            if (streamFVF.contains(FVF.VERT_COMPR)) {
                accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.POSITION, readCompressedPosition(compressParams));
                if (streamFVF.contains(FVF.NORM_IN_VERT4)) {
                    bufferOffset += 6;
                } else {
                    bufferOffset += 8;
                }
            } else {
                accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.POSITION, readVector3());
                bufferOffset += 12;
            }
            source.position(0);
            attributes.add(accessor.read(source));
        }

        if (streamFVF.contains(FVF.NORM) && streamFVF.contains(FVF.NORM_IN_VERT4)) {
            GeoAccessor<?> accessor;
            if (streamFVF.contains(FVF.NORM_COMPR)) {
                accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.NORMAL, readCompressedS16Normals());
                bufferOffset += 2;
            } else {
                accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.NORMAL, readCompressedF32Normals());
                bufferOffset += 4;
            }
            source.position(0);
            attributes.add(accessor.read(source));
        }

        if (streamFVF.contains(FVF.WEIGHT4)) {
            var accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.weights(0, ComponentType.UNSIGNED_BYTE), (source1, buffer) -> {
                byte[] weights = new byte[4];
                weights[0] = source1.readByte();
                weights[1] = source1.readByte();
                weights[2] = source1.readByte();
                weights = renormalize(weights);
                source1.readByte();
                buffer.put(weights);
            });
            bufferOffset += 4;
            source.position(0);
            attributes.add(accessor.read(source));
        }
        if (streamFVF.contains(FVF.WEIGHT8)) {
            var accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.weights(0, ComponentType.UNSIGNED_BYTE), (source1, buffer) -> {
                byte[] weights = new byte[4];
                weights[0] = source1.readByte();
                weights[1] = source1.readByte();
                weights[2] = source1.readByte();
                weights[3] = source1.readByte();
                weights = renormalize(weights);
                buffer.put(weights);
            });
            bufferOffset += 4;
            source.position(0);
            attributes.add(accessor.read(source));
            accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.weights(1, ComponentType.UNSIGNED_BYTE), (source1, buffer) -> {
                byte[] weights = new byte[4];
                weights[0] = source1.readByte();
                weights[1] = source1.readByte();
                weights[2] = source1.readByte();
                source1.readByte();
                weights = renormalize(weights);
                buffer.put(weights);
            });
            bufferOffset += 4;
            source.position(0);
            attributes.add(accessor.read(source));
        }

        if (streamFVF.contains(FVF.INDICES)) {
            if (!streamFVF.contains(FVF.WEIGHT4) && !streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.joints(0, ComponentType.UNSIGNED_BYTE), readByteBoneIndices(boneMap));
                bufferOffset += 4;
                source.position(0);
                attributes.add(accessor.read(source));
                var weights = new byte[vertCount * 4];
                weights[0] = (byte) 255;
                attributes.add(new VertexBuffer(ByteBuffer.wrap(weights), VertexBufferInfo.weights(0, ComponentType.UNSIGNED_BYTE)));
            }

            if (streamFVF.contains(FVF.WEIGHT4) || streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.joints(0, ComponentType.UNSIGNED_BYTE), readByteBoneIndices(boneMap));
                bufferOffset += 4;
                source.position(0);
                attributes.add(accessor.read(source));
            }
            if (streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.joints(1, ComponentType.UNSIGNED_BYTE), readByteBoneIndices(boneMap));
                bufferOffset += 4;
                source.position(0);
                attributes.add(accessor.read(source));
            }

        }
        source.position(0);
        if (streamFVF.contains(FVF.INDICES16)) {
            if (!streamFVF.contains(FVF.WEIGHT4) && !streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.joints(0, ComponentType.UNSIGNED_SHORT), readShortBoneIndices(boneMap));
                bufferOffset += 8;
                source.position(0);
                attributes.add(accessor.read(source));

                var weights = new byte[vertCount * 4];
                weights[0] = (byte) 255;
                attributes.add(new VertexBuffer(ByteBuffer.wrap(weights), VertexBufferInfo.weights(0, ComponentType.UNSIGNED_BYTE)));
            }

            if (streamFVF.contains(FVF.WEIGHT4) || streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.joints(0, ComponentType.UNSIGNED_SHORT), readShortBoneIndices(boneMap));
                bufferOffset += 8;
                source.position(0);
                attributes.add(accessor.read(source));
            }
            if (streamFVF.contains(FVF.WEIGHT8)) {
                var accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.joints(1, ComponentType.UNSIGNED_SHORT), readShortBoneIndices(boneMap));
                bufferOffset += 8;
                source.position(0);
                attributes.add(accessor.read(source));
            }
        }

        for (FVF tangentLayerFlag : EnumSet.range(FVF.TANG0, FVF.TANG4)) {
            if (streamFVF.contains(tangentLayerFlag)) {
                source.position(0);
                Check.state(streamFVF.contains(FVF.TANG_COMPR));
                if (streamFVF.contains(FVF.TANG_COMPR)) {
                    bufferOffset += 4;
                }
            }
        }

        int colorLayerCount = 0;
        for (FVF colorLayerFlag : EnumSet.range(FVF.COLOR0, FVF.COLOR5)) {
            if (streamFVF.contains(colorLayerFlag)) {
                var accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.colors(colorLayerCount, ComponentType.UNSIGNED_BYTE), (source1, buffer) -> {
                    var color = source1.readBytes(4);
                    if (color[0] == 0 && color[1] == 0 && color[2] == 0) { // Avoid full black VColors for now
                        color[0] = -1;
                        color[1] = -1;
                        color[2] = -1;
                    }
                    buffer.put(color);
                });
                source.position(0);
                attributes.add(accessor.read(source));
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
                var accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.texCoords(uvLayerCount), (source1, buffer) -> {
                    float u = MathF.unpackSNorm16(source1.readShort());
                    float v = MathF.unpackSNorm16(source1.readShort());

                    if (uvTile > 1) {
                        u *= (uvTile);
                        v *= (uvTile);
                    }

                    buffer.put(u);
                    buffer.put(v);
                });
                source.position(0);
                attributes.add(accessor.read(source));
                uvLayerCount++;
                bufferOffset += 4;
            }
            uvLayerId++;
        }

        if (streamFVF.contains(FVF.NORM) && !streamFVF.contains(FVF.NORM_IN_VERT4)) {
            var accessor = new GeoAccessor<>(bufferOffset, vertCount, vertexStream.stride, VertexBufferInfo.NORMAL, readVector3());
            bufferOffset += 12;
            source.position(0);
            attributes.add(accessor.read(source));
        }

        Check.state(bufferOffset <= vertexStream.stride);
    }

    // long objGetFVFOffset(ExtendedSet<FVF> geomFvf, FVF componentFvf) {
    //     long offset; // rax
    //     long v3; // edx
    //     long v4; // r8d
    //     long v5; // edx
    //     long v6; // ecx
    //     long v7; // ecx
    //     long v8; // r8d
    //     long v9; // eax
    //     long v10; // eax
    //     long v11; // eax
    //     long v12; // eax
    //     long v13; // eax
    //     long v14; // eax
    //
    //     offset = 0;
    //     if (componentFvf == FVF.VERT || componentFvf == FVF.NORM && geomFvf.containsOnly(FVF.NORM_IN_VERT4))
    //         return offset;
    //     offset = 0;
    //     if (geomFvf.contains(FVF.VERT)) {
    //         offset = 8;
    //         if (!geomFvf.contains(FVF.VERT_COMPR))
    //             offset = (geomFvf.contains(FVF.NORM_IN_VERT4) ? 1 : 0) + 12;// ((geomFvf.data >> 10) & 0x4)
    //     }
    //     if (componentFvf == FVF.WEIGHT4 || componentFvf == FVF.WEIGHT8)
    //         return offset;
    //     if (geomFvf.containsAnyOf(FVF.WEIGHT8, FVF.WEIGHT4)) {
    //         v3 = 8;
    //         if (!geomFvf.contains(FVF.WEIGHT8))           // (geomFvf.data & 0x80) == 0
    //             v3 = 4 * (geomFvf.containsAnyOf(FVF.INDICES16, FVF.INDICES, FVF.WEIGHT4) ? 1 : 0);
    //         offset = (v3 + offset);
    //     }
    //     if (componentFvf == FVF.INDICES || componentFvf == FVF.INDICES16)
    //         return offset;
    //     if (geomFvf.containsAnyOf(FVF.INDICES16, FVF.INDICES)) {
    //         v4 = 8;
    //         if (!geomFvf.contains(FVF.WEIGHT8))           // (geomFvf.data & 0x80) == 0
    //             v4 = 4 * (geomFvf.containsAnyOf(FVF.INDICES16, FVF.INDICES, FVF.WEIGHT4) ? 1 : 0);
    //         v5 = 2;
    //         if (!geomFvf.contains(FVF.INDICES16))
    //             v5 = geomFvf.contains(FVF.INDICES) ? 1 : 0;    // (geomFvf.data >> 8) & 1
    //         offset = (v4 * v5 + offset);
    //     }
    //     if (componentFvf == FVF.MASKING_FLAGS)
    //         return offset;
    //     v6 = offset + 4;
    //     if ((geomFvf.data & FVF.MASKING_FLAGS) == 0)
    //         v6 = offset;
    //     if (componentFvf == FVF.NORM)
    //         return v6;
    //     if ((geomFvf.data & (FVF.NORM_IN_VERT4 | FVF.NORM)) == FVF.NORM) {
    //         if ((geomFvf.data & FVF.NORM_COMPR) != 0)
    //             v6 += 4;
    //         else
    //             v6 += 12;
    //     }
    //     if ((geomFvf.data & FVF.TANG_COMPR) == 0) {
    //         if (componentFvf == FVF.TANG0)
    //             return v6;
    //         offset = v6 + 16;
    //         if ((geomFvf.data & FVF.TANG0) == 0)
    //             offset = v6;
    //         if (componentFvf == FVF.TANG1)
    //             return offset;
    //         if ((geomFvf.data & FVF.TANG1) != 0)
    //             offset = (offset + 16);
    //         if (componentFvf == FVF.TANG2)
    //             return offset;
    //         if ((geomFvf.data & FVF.TANG2) != 0)
    //             offset = (offset + 16);
    //         if (componentFvf == FVF.TANG3)
    //             return offset;
    //         if ((geomFvf.data & FVF.TANG3) != 0)
    //             offset = (offset + 16);
    //         if (componentFvf == FVF.TANG4)
    //             return offset;
    //         v7 = offset + 16;
    //         LABEL_60:
    //         if ((geomFvf.data & FVF.TANG4) == 0)
    //             v7 = offset;
    //         offset = v7;
    //         if (componentFvf == FVF.COLOR0)
    //             return offset;
    //         if ((geomFvf.data & FVF.COLOR0) != 0)
    //             offset = v7 + 4;
    //         if (componentFvf == FVF.COLOR1)
    //             return offset;
    //         if ((geomFvf.data & FVF.COLOR1) != 0)
    //             offset = (offset + 4);
    //         if (componentFvf == FVF.COLOR2)
    //             return offset;
    //         if ((geomFvf.data & FVF.COLOR2) != 0)
    //             offset = (offset + 4);
    //         if (componentFvf == FVF.COLOR3)
    //             return offset;
    //         if ((geomFvf.data & FVF.COLOR3) != 0)
    //             offset = (offset + 4);
    //         if (componentFvf == FVF.COLOR4)
    //             return offset;
    //         if ((geomFvf.data & FVF.COLOR4) != 0)
    //             offset = (offset + 4);
    //         if (componentFvf == FVF.COLOR5)
    //             return offset;
    //         if ((geomFvf.data & FVF.COLOR5) != 0)
    //             offset = (offset + 4);
    //         if (componentFvf == FVF.BS_INFO)
    //             return offset;
    //         v8 = offset + 16;
    //         if ((geomFvf.data & FVF.BS_INFO) == 0)
    //             v8 = offset;
    //         if (componentFvf == FVF.TEX0)
    //             return v8;
    //         if ((geomFvf.data & FVF.TEX0) != 0) {
    //             v9 = ((geomFvf.data & FVF.TEX0_4D) != 0) + 1;
    //             if (SLODWORD(geomFvf.data) < 0)         // (geomFvf.data & 0x80000000) != 0
    //             {
    //                 if ((~geomFvf.data & (FVF.TEX0_4D_BYTE | FVF.TEX0_4D)) != 0)
    //                     v8 += 4 * v9;
    //                 else
    //                     v8 += 4;
    //             } else {
    //                 v8 += 8 * v9;
    //             }
    //         }
    //         if (componentFvf == FVF.TEX1)
    //             return v8;
    //         if ((geomFvf.data & FVF.TEX1) != 0) {
    //             v10 = ((geomFvf.data & FVF.TEX1_4D) != 0) + 1;
    //             if ((geomFvf.data & FVF.TEX1_COMPR) != 0) {
    //                 if ((~geomFvf.data & (FVF.TEX1_4D_BYTE | FVF.TEX1_4D)) != 0)
    //                     v8 += 4 * v10;
    //                 else
    //                     v8 += 4;
    //             } else {
    //                 v8 += 8 * v10;
    //             }
    //         }
    //         if (componentFvf == FVF.TEX2)
    //             return v8;
    //         if ((geomFvf.data & FVF.TEX2) != 0) {
    //             v11 = ((geomFvf.data & FVF.TEX2_4D) != 0) + 1;
    //             if ((geomFvf.data & FVF.TEX2_COMPR) != 0) {
    //                 if ((~geomFvf.data & (FVF.TEX2_4D_BYTE | FVF.TEX2_4D)) != 0)
    //                     v8 += 4 * v11;
    //                 else
    //                     v8 += 4;
    //             } else {
    //                 v8 += 8 * v11;
    //             }
    //         }
    //         if (componentFvf == FVF.TEX3)
    //             return v8;
    //         if ((geomFvf.data & FVF.TEX3) != 0) {
    //             v12 = ((geomFvf.data & FVF.TEX3_4D) != 0) + 1;
    //             if ((geomFvf.data & FVF.TEX3_COMPR) != 0) {
    //                 if ((~geomFvf.data & (FVF.TEX3_4D_BYTE | FVF.TEX3_4D)) != 0)
    //                     v8 += 4 * v12;
    //                 else
    //                     v8 += 4;
    //             } else {
    //                 v8 += 8 * v12;
    //             }
    //         }
    //         if (componentFvf == FVF.TEX4)
    //             return v8;
    //         if ((geomFvf.data & FVF.TEX4) != 0) {
    //             v13 = ((geomFvf.data & FVF.TEX4_4D) != 0) + 1;
    //             if ((geomFvf.data & FVF.TEX4_COMPR) != 0) {
    //                 if ((~geomFvf.data & (FVF.TEX4_4D_BYTE | FVF.TEX4_4D)) != 0)
    //                     v8 += 4 * v13;
    //                 else
    //                     v8 += 4;
    //             } else {
    //                 v8 += 8 * v13;
    //             }
    //         }
    //         if (componentFvf == FVF.TEX5 || (geomFvf.data & FVF.TEX5) == 0) {
    //             return v8;
    //         } else {
    //             v14 = ((geomFvf.data & FVF.TEX5_4D) != 0) + 1;
    //             if ((geomFvf.data & FVF.TEX5_COMPR) != 0) {
    //                 if ((~geomFvf.data & (FVF.TEX5_4D_BYTE | FVF.TEX5_4D)) != 0)
    //                     return v8 + 4 * v14;
    //                 else
    //                     return v8 + 4;
    //             } else {
    //                 return v8 + 8 * v14;
    //             }
    //         }
    //         return offset;
    //     }
    //     if (componentFvf == FVF.TANG0)
    //         return v6;
    //     offset = v6 + 4;
    //     if ((geomFvf.data & FVF.TANG0) == 0)
    //         offset = v6;
    //     if (componentFvf == FVF.TANG1)
    //         return offset;
    //     if ((geomFvf.data & FVF.TANG1) != 0)
    //         offset = (offset + 4);
    //     if (componentFvf == FVF.TANG2)
    //         return offset;
    //     if ((geomFvf.data & FVF.TANG2) != 0)
    //         offset = (offset + 4);
    //     if (componentFvf == FVF.TANG3)
    //         return offset;
    //     if ((geomFvf.data & FVF.TANG3) != 0)
    //         offset = (offset + 4);
    //     if (componentFvf == FVF.TANG4)
    //         return offset;
    //     v7 = offset + 4;
    //     goto LABEL_60;
    // }
}
