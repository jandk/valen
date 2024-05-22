package be.twofold.valen.export.gltf.model;

import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface TextureDef extends GltfChildOfRootProperty {
    Optional<SamplerSchema> getSampler();

    Optional<ImageId> getSource();
}
