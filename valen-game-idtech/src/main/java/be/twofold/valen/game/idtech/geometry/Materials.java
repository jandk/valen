package be.twofold.valen.game.idtech.geometry;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.material.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public final class Materials {
    private static final Logger LOG = LoggerFactory.getLogger(Materials.class);

    private Materials() {
    }

    /**
     * Returns the meshes with their material and name applied.
     * <p>
     * The meshes handed in may well be immutable, so this copies rather than assigning in place.
     *
     * @param materialAssetId Turns a material name into an asset ID, which is the only part of
     *                        this that differs per game.
     */
    public static <T> List<Mesh> apply(
        LoadingContext context,
        List<Mesh> meshes,
        List<T> meshInfos,
        Function<String, AssetID> materialAssetId,
        Function<T, String> materialNameMapper,
        Function<T, String> meshNameMapper
    ) throws IOException {
        var materials = new HashMap<String, Material>();
        var result = new ArrayList<>(meshes);
        for (int i = 0; i < result.size(); i++) {
            var meshInfo = meshInfos.get(i);
            var materialName = materialNameMapper.apply(meshInfo);
            var meshName = meshNameMapper.apply(meshInfo);
            if (!materials.containsKey(materialName)) {
                var assetId = materialAssetId.apply(materialName);
                if (context.exists(assetId)) {
                    var material = context.load(assetId, Material.class);
                    materials.put(materialName, material);
                } else {
                    LOG.warn("Could not load material {}", materialName);
                }
            }
            result.set(i, result.get(i).toBuilder()
                .name(meshName)
                .material(materials.get(materialName))
                .build());
        }
        return List.copyOf(result);
    }
}
