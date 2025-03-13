package be.twofold.valen.game.eternal.reader.geometry;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public final class Materials {
    private static final Logger LOG = LoggerFactory.getLogger(Materials.class);

    private Materials() {
    }

    public static <T> void apply(
        EternalArchive archive,
        List<Mesh> meshes,
        List<T> meshInfos,
        Function<T, String> materialNameMapper,
        Function<T, String> meshNameMapper
    ) throws IOException {
        var materials = new HashMap<String, Material>();
        for (int i = 0; i < meshes.size(); i++) {
            var meshInfo = meshInfos.get(i);
            var materialName = materialNameMapper.apply(meshInfo);
            var meshName = meshNameMapper.apply(meshInfo);
            if (!materials.containsKey(materialName)) {
                var materialFile = "generated/decls/material2/" + materialName + ".decl";
                var assetId = EternalAssetID.from(materialFile, ResourceType.RsStreamFile);
                if (archive.getAsset(assetId).isPresent()) {
                    var material = archive.loadAsset(assetId, Material.class);
                    materials.put(materialName, material);
                } else {
                    LOG.warn("Could not load material {}", materialName);
                }
            }
            meshes.set(i, meshes.get(i)
                .withName(Optional.ofNullable(meshName))
                .withMaterial(Optional.ofNullable(materials.get(materialName))));
        }
    }
}
