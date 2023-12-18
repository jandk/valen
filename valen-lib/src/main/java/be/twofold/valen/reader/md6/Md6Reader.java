package be.twofold.valen.reader.md6;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.geometry.*;

import java.util.*;

public final class Md6Reader {
    private final FileManager fileManager;

    public Md6Reader(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Md6 read(BetterBuffer buffer) {
        return read(buffer, false, 0);
    }

    public Md6 read(BetterBuffer buffer, boolean readStreams, long hash) {
        var md6 = Md6.read(buffer);

        List<Mesh> meshes;
        if (readStreams) {
            meshes = readStreamedGeometry(md6, 0, hash);
            fixJointIndices(md6, meshes);
        } else {
            meshes = List.of();
        }
        return md6.withMeshes(meshes);
    }

    private List<Mesh> readStreamedGeometry(Md6 md6, int lod, long hash) {
        var streamHash = (hash << 4) | lod;
        var size = md6.layouts().get(lod).uncompressedSize();
        var buffer = fileManager.readStream(streamHash, size);

        var lodInfos = md6.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = md6.layouts().get(lod).memoryLayouts();

        return new GeometryReader(true)
            .readMeshes(buffer, lodInfos, layouts);
    }

    private void fixJointIndices(Md6 md6, List<Mesh> meshes) {
        var bones = md6.boneInfo().bones();

        // This lookup table is in reverse... Nice
        var lookup = new byte[bones.length];
        for (var i = 0; i < bones.length; i++) {
            lookup[Byte.toUnsignedInt(bones[i])] = (byte) i;
        }

        for (var i = 0; i < md6.meshInfos().size(); i++) {
            var meshInfo = md6.meshInfos().get(i);
            var joints = meshes.get(i).joints();

            var array = joints.array();
            for (var j = 0; j < array.length; j++) {
                array[j] = lookup[(array[j] & 0xff) + meshInfo.unknown2()];
            }
        }
    }
}
