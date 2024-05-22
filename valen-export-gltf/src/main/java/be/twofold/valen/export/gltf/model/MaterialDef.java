package be.twofold.valen.export.gltf.model;

import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface MaterialDef extends GltfChildOfRootProperty {
    Optional<PbrMetallicRoughnessSchema> getPbrMetallicRoughness();

    Optional<NormalTextureInfoSchema> getNormalTexture();

}
