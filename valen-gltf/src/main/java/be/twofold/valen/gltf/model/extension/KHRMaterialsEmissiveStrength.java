package be.twofold.valen.gltf.model.extension;

import be.twofold.valen.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface KHRMaterialsEmissiveStrength extends GltfProperty, Extension {
    @Override
    default String getName() {
        return "KHR_materials_emissive_strength";
    }

    @Override
    default boolean isRequired() {
        return false;
    }

    /**
     * The strength adjustment to be multiplied with the material's emissive value.
     */
    Optional<Float> getEmissiveStrength();
}
