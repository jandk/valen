package be.twofold.valen.reader.model;

import be.twofold.valen.geometry.*;
import be.twofold.valen.reader.geometry.*;

import java.util.*;

public record Model(
    ModelHeader header,
    List<ModelMeshInfo> meshInfos,
    List<List<ModelLodInfo>> lodInfos,
    ModelSettings settings,
    ModelBooleans booleans,
    List<List<GeometryMemoryLayout>> streamInfos,
    List<GeometryDiskLayout> streamLayouts,
    List<Mesh> meshes
) {
}
