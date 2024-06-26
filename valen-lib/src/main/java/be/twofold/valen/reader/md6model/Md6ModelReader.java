package be.twofold.valen.reader.md6model;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.reader.geometry.*;
import be.twofold.valen.resource.*;
import dagger.*;
import jakarta.inject.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class Md6ModelReader implements ResourceReader<Model> {
    private final Lazy<FileManager> fileManager;

    @Inject
    Md6ModelReader(Lazy<FileManager> fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.BaseModel;
    }

    @Override
    public Model read(DataSource source, Resource resource) throws IOException {
        Md6Model md6 = read(source, true, resource.hash());
        return new Model(md6.meshes(), null, null);
    }

    public Md6Model read(DataSource source, boolean readStreams, long hash) throws IOException {
        var md6 = Md6Model.read(source);

        List<Mesh> meshes;
        if (readStreams) {
            meshes = readStreamedGeometry(md6, 0, hash);
            fixJointIndices(md6, meshes);
        } else {
            meshes = List.of();
        }
        return md6.withMeshes(meshes);
    }

    private List<Mesh> readStreamedGeometry(Md6Model md6, int lod, long hash) throws IOException {
        var uncompressedSize = md6.layouts().get(lod).uncompressedSize();
        if (uncompressedSize == 0) {
            return List.of();
        }

        var lodInfos = md6.meshInfos().stream()
            .<LodInfo>map(mi -> mi.lodInfos().get(lod))
            .toList();
        var layouts = md6.layouts().get(lod).memoryLayouts();

        var identity = (hash << 4) | lod;
        var source = new ByteArrayDataSource(fileManager.get().readStream(identity, uncompressedSize));
        return GeometryReader.readStreamedMesh(source, lodInfos, layouts, true);
    }

    private void fixJointIndices(Md6Model md6, List<Mesh> meshes) {
        var jointRemap = md6.boneInfo().jointRemap();

        // This lookup table is in reverse... Nice
        var lookup = new byte[jointRemap.length];
        for (var i = 0; i < jointRemap.length; i++) {
            lookup[Byte.toUnsignedInt(jointRemap[i])] = (byte) i;
        }

        for (var i = 0; i < meshes.size(); i++) {
            var meshInfo = md6.meshInfos().get(i);
            var joints = meshes.get(i)
                .getBuffer(Semantic.Joints0)
                .orElseThrow();

            // Just assume it's a byte buffer, because we read it as such
            var array = ((ByteBuffer) joints.buffer()).array();
            for (var j = 0; j < array.length; j++) {
                array[j] = lookup[Byte.toUnsignedInt(array[j]) + meshInfo.unknown2()];
            }
        }
    }
}
