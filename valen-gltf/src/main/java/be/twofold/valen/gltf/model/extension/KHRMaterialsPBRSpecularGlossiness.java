package be.twofold.valen.gltf.model.extension;

import be.twofold.valen.gltf.model.*;
import be.twofold.valen.gltf.model.texture.*;
import be.twofold.valen.gltf.types.*;
import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface KHRMaterialsPBRSpecularGlossiness extends GltfProperty, Extension {
    @Override
    default String getName() {
        return "KHR_materials_pbrSpecularGlossiness";
    }

    @Override
    default boolean isRequired() {
        return false;
    }

    /**
     * The RGBA components of the reflected diffuse color of the material. Metals have a diffuse value of `[0.0, 0.0,
     * 0.0]`. The fourth component (A) is the alpha coverage of the material. The `alphaMode` property specifies how
     * alpha is interpreted. The values are linear.
     */
    Optional<Vec4> getDiffuseFactor();

    /**
     * The diffuse texture. This texture contains RGB components of the reflected diffuse color of the material encoded
     * with the sRGB transfer function. If the fourth component (A) is present, it represents the linear alpha coverage
     * of the material. Otherwise, an alpha of 1.0 is assumed. The `alphaMode` property specifies how alpha is
     * interpreted. The stored texels must not be premultiplied.
     */
    Optional<TextureInfo> getDiffuseTexture();

    /**
     * The specular RGB color of the material. This value is linear.
     */
    Optional<Vec3> getSpecularFactor();

    /**
     * The glossiness or smoothness of the material. A value of 1.0 means the material has full glossiness or is
     * perfectly smooth. A value of 0.0 means the material has no glossiness or is completely rough. This value is
     * linear.
     */
    OptionalDouble getGlossinessFactor();

    /**
     * The specular-glossiness texture is an RGBA texture, containing the specular color (RGB) encoded with the sRGB
     * transfer function and the linear glossiness value (A).
     */
    Optional<TextureInfo> getSpecularGlossinessTexture();
}
