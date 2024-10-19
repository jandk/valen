package org.redeye.valen.game.spacemarines2.converters;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.types.template.*;

import java.io.*;
import java.util.*;

public class GeometryManagerToModel {


    public Model convert(Archive archive, EmperorAssetId assetId, EmperorAssetId resourceId, GeometryManager geometryManager, List<LodDef> lodDef) throws IOException {
        var materialConverter = new MaterialConverter();
        // ArrayList<Material> materials = materialConverter.convertMaterials(archive, resourceId);
        ArrayList<Material> materials = new ArrayList<>();


        var skeletonConverter = new SkeletonConverter();
        var boneMap = new HashMap<Integer, Integer>();
        var skeleton = geometryManager.rootObjId != null ? skeletonConverter.convertSkeleton(geometryManager, boneMap) : null;


        var streams = geometryManager.streams;
        List<ObjSplit> splits = geometryManager.splits;

        List<Short> lod0Ids = lodDef != null ? lodDef.stream().filter(ld -> ld.index == 0).map(ld -> ld.objId).toList() : List.of();
        ArrayList<Mesh> meshes = new ArrayList<>();
        var meshConverter = new MeshConverter(boneMap);
        if (geometryManager.objSplitInfo != null) {
            meshes.addAll(meshConverter.extractBySplitInfo(geometryManager, lod0Ids, splits, streams, materials));
        } else {
            for (ObjSplit split : splits) {
                meshConverter.convertSplitMesh(split, streams, materials).ifPresent(meshes::add);
            }
        }
        String modelName = assetId.fileName().substring(0, assetId.fileName().indexOf('.'));
        return new Model(modelName, meshes, skeleton);
    }


}
