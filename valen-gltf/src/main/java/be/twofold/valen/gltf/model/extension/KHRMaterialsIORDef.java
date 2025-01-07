package be.twofold.valen.gltf.model.extension;

import be.twofold.valen.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface KHRMaterialsIORDef extends GltfProperty, Extension {
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
     * <p>
     * The index of refraction (IOR) is a measured physical number usually in the range between 1 and 2 that determines
     * how much the path of light is bent, or refracted, when entering a material. It also influences the ratio between
     * reflected and transmitted light, calculated from the Fresnel equations.
     */
    OptionalDouble getIor();
}
