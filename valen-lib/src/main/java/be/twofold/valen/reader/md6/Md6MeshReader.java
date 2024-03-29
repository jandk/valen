package be.twofold.valen.reader.md6;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.geometry.*;
import be.twofold.valen.resource.*;
import be.twofold.valen.stream.*;
import jakarta.inject.*;

import java.nio.*;
import java.util.*;

public final class Md6MeshReader implements ResourceReader<Model> {
    private final StreamManager streamManager;

    @Inject
    public Md6MeshReader(StreamManager streamManager) {
        this.streamManager = streamManager;
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.BaseModel;
    }

    @Override
    public Model read(BetterBuffer buffer, Resource resource) {
        Md6Mesh md6 = read(buffer, true, resource.hash());
        return new Model(md6.meshes(), null, null);
    }

    public Md6Mesh read(BetterBuffer buffer, boolean readStreams, long hash) {
        var md6 = Md6Mesh.read(buffer);

        List<Mesh> meshes;
        if (readStreams) {
            meshes = readStreamedGeometry(md6, 0, hash);
            fixJointIndices(md6, meshes);
        } else {
            meshes = List.of();
        }
        return md6.withMeshes(meshes);
    }

    private List<Mesh> readStreamedGeometry(Md6Mesh md6, int lod, long hash) {
        var identity = (hash << 4) | lod;
        var uncompressedSize = md6.layouts().get(lod).uncompressedSize();
        var buffer = BetterBuffer.wrap(streamManager.read(identity, uncompressedSize));

        var lodInfos = md6.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = md6.layouts().get(lod).memoryLayouts();

        return new GeometryReader(true)
            .readMeshes(buffer, lodInfos, layouts);
    }

    private void fixJointIndices(Md6Mesh md6, List<Mesh> meshes) {
        var bones = md6.boneInfo().bones();

        // This lookup table is in reverse... Nice
        var lookup = new byte[bones.length];
        for (var i = 0; i < bones.length; i++) {
            lookup[Byte.toUnsignedInt(bones[i])] = (byte) i;
        }

        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = md6.meshInfos().get(i);
            var joints = meshes.get(i)
                .getBuffer(Semantic.Joints)
                .orElseThrow();

            // Just assume it's a byte buffer, because we read it as such
            var array = ((ByteBuffer) joints.buffer()).array();
            for (var j = 0; j < array.length; j++) {
                array[j] = lookup[Byte.toUnsignedInt(array[j]) + meshInfo.unknown2()];
            }
        }
    }
}
