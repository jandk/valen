package be.twofold.valen.reader.geometry;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;

import java.nio.*;
import java.util.*;
import java.util.stream.*;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public final class GeometryReader {
    private GeometryReader() {
    }

    public static List<Mesh> readMeshes(BetterBuffer buffer, List<LodInfo> lods, List<GeometryMemoryLayout> layouts) {
        List<FloatBuffer> vertexBuffers = new ArrayList<>();
        for (GeometryMemoryLayout layout : layouts) {
            buffer.position(layout.positionOffset());
            lods.stream()
                .filter(lod -> lod.flags() == layout.combinedVertexMask())
                .map(lod -> switch (layout.positionMask()) {
                    case 0x01 -> Geometry.readVertices(buffer, lod);
                    case 0x20 -> Geometry.readPackedVertices(buffer, lod);
                    default -> throw new RuntimeException("Unknown position mask: " + layout.positionMask());
                })
                .forEach(vertexBuffers::add);
        }

        List<FloatBuffer> normalBuffers = new ArrayList<>();
        for (GeometryMemoryLayout layout : layouts) {
            buffer.position(layout.normalOffset());
            lods.stream()
                .filter(lod -> lod.flags() == layout.combinedVertexMask())
                .map(lod -> switch (layout.normalMask()) {
                    case 0x14 -> Geometry.readPackedNormals(buffer, lod);
                    default -> throw new RuntimeException("Unknown normal mask: " + layout.normalMask());
                })
                .forEach(normalBuffers::add);
        }

        List<FloatBuffer> uvBuffers = new ArrayList<>();
        for (GeometryMemoryLayout layout : layouts) {
            buffer.position(layout.uvOffset());
            lods.stream()
                .filter(lod -> lod.flags() == layout.combinedVertexMask())
                .map(lod -> switch (layout.uvMask()) {
                    case 0x08000 -> Geometry.readUVs(buffer, lod);
                    case 0x20000 -> Geometry.readPackedUVs(buffer, lod);
                    default -> throw new RuntimeException("Unknown UV mask: " + layout.normalMask());
                })
                .forEach(uvBuffers::add);
        }

        List<ShortBuffer> indexBuffers = new ArrayList<>();
        for (GeometryMemoryLayout layout : layouts) {
            buffer.position(layout.indexOffset());
            lods.stream()
                .filter(lod -> lod.flags() == layout.combinedVertexMask())
                .map(lod -> Geometry.readFaces(buffer, lod))
                .forEach(indexBuffers::add);
        }

        return IntStream.range(0, lods.size())
            .mapToObj(i -> new Mesh(vertexBuffers.get(i), normalBuffers.get(i), uvBuffers.get(i), indexBuffers.get(i)))
            .toList();
    }
}
