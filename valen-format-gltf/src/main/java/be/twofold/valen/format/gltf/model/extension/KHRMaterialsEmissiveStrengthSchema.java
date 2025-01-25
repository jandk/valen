package be.twofold.valen.format.gltf.model.extension;

import be.twofold.valen.format.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

/**
 * glTF extension that adjusts the strength of emissive material properties.
 */
@Schema2Style
@Value.Immutable
public interface KHRMaterialsEmissiveStrengthSchema extends GltfProperty, Extension {

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
