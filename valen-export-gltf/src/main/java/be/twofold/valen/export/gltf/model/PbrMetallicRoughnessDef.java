package be.twofold.valen.export.gltf.model;

import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface PbrMetallicRoughnessDef extends GltfChildOfRootProperty {
    Optional<float[]> getBaseColorFactor();

    Optional<TextureInfoSchema> getBaseColorTexture();

    Optional<Float> getMetallicFactor();

    Optional<Float> getRoughnessFactor();

    Optional<TextureInfoSchema> getMetallicRoughnessTexture();

}
