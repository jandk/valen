package be.twofold.valen.gltf.model.extension;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.texture.*;
import be.twofold.valen.gltf.types.*;
import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface KHRMaterialsSpecularDef extends GltfProperty, Extension {
    @Override
    default String getName() {
        return "KHR_materials_specular";
    }

    @Override
    default boolean isRequired() {
        return false;
    }

    /**
     * The strength of the specular reflection.
     */
    OptionalDouble getSpecularFactor();

    /**
     * A texture that defines the specular factor in the alpha channel.
     */
    Optional<TextureInfo> getSpecularTexture();

    /**
     * The F0 RGB color of the specular reflection.
     */
    Optional<Vec3> getSpecularColorFactor();

    /**
     * A texture that defines the F0 color of the specular reflection.
     */
    Optional<TextureInfo> getSpecularColorTexture();
}
