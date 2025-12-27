package be.twofold.valen.format.gltf.model.extension;

import be.twofold.valen.format.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

/**
 * glTF extension that defines the index of refraction of a material.
 */
@SchemaStyle
@Value.Immutable
public interface KHRMaterialsIorSchema extends GltfProperty, Extension {

    @Override
    default String getName() {
        return "KHR_materials_ior";
    }

    @Override
    default boolean isRequired() {
        return false;
    }

    /**
     * The index of refraction.
     */
    Optional<Double> getIor();

}
