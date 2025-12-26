package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.greatcircle.reader.md6skl.*;

import java.io.*;
import java.util.*;

public record DeformModel(
    DeformModelHeader header,
    List<DeformModelMesh> meshes,
    List<GeometryMemoryLayout> memoryLayouts,
    int unknown1,
    List<GeometryDiskLayout> diskLayouts,
    Md6Skl skeleton1,
    Md6Skl skeleton2,
    Shorts unknown2,
    List<DeformModelEntry> entryList1,
    List<DeformModelEntry> entryList2,
    Bounds bounds
) {
    public static DeformModel read(BinarySource source) throws IOException {
        var header = DeformModelHeader.read(source);
        var meshes = source.readObjects(source.readInt(), DeformModelMesh::read);
        var numStreams = source.readInt();
        var memoryLayouts = source.readObjects(numStreams, GeometryMemoryLayout::read);
        var unknown1 = source.readInt();
        var diskLayouts = new ArrayList<GeometryDiskLayout>();
        for (var i = 0; i < numStreams; i++) {
            var diskLayout = GeometryDiskLayout.read(source, memoryLayouts.subList(i, i + 1));
            diskLayouts.add(diskLayout);
        }

        var skeleton1 = (Md6Skl) null;
        var skeleton2 = (Md6Skl) null;
        var unknown2 = (Shorts) null;
        var skeletonSize = source.readInt();
        if (skeletonSize != 0) {
            var skeleton1Length = source.readInt();
            if (skeleton1Length != 0) {
                skeleton1 = Md6Skl.read(source);
            }
            var skeleton2Length = source.readInt();
            if (skeleton2Length != 0) {
                skeleton2 = Md6Skl.read(source);
            }
            unknown2 = source.readShorts(source.readInt());
        }

        var entryList1 = source.readObjects(source.readInt(), DeformModelEntry::read);
        var entryList2 = source.readObjects(source.readInt(), DeformModelEntry::read);
        var bounds = Bounds.read(source);

        return new DeformModel(
            header,
            meshes,
            memoryLayouts,
            unknown1,
            diskLayouts,
            skeleton1,
            skeleton2,
            unknown2,
            entryList1,
            entryList2,
            bounds
        );
    }
}
