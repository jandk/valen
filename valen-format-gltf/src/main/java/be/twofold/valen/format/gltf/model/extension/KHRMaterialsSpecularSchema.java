package be.twofold.valen.format.gltf.model.extension;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.texture.*;
import be.twofold.valen.format.gltf.types.*;
import org.immutables.value.*;

import java.util.*;

/**
 * glTF extension that defines the strength of the specular reflection.
 */
@SchemaStyle
@Value.Immutable
public interface KHRMaterialsSpecularSchema extends GltfProperty, Extension {
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
    Optional<Float> getSpecularFactor();

    /**
     * A texture that defines the specular factor in the alpha channel.
     */
    Optional<TextureInfoSchema> getSpecularTexture();

    /**
     * The F0 RGB color of the specular reflection.
     */
    Optional<Vec3> getSpecularColorFactor();

    /**
     * A texture that defines the F0 color of the specular reflection.
     */
    Optional<TextureInfoSchema> getSpecularColorTexture();

}
