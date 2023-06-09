package be.twofold.valen.reader.md6;

import be.twofold.valen.geometry.*;
import be.twofold.valen.reader.geometry.*;

import java.util.*;

public record Md6(
    Md6Header header,
    Md6BoneInfo boneInfo,
    List<Md6MeshInfo> meshInfos,
    List<Md6MaterialInfo> materialInfos,
    List<GeometryMemoryLayout> memoryLayouts,
    List<GeometryDiskLayout> diskLayouts,
    List<Mesh> meshes
) {
}
