package be.twofold.valen.format.gltf.model.camera;

import be.twofold.valen.format.gltf.model.*;
import org.immutables.value.*;

/**
 * An orthographic camera containing properties to create an orthographic projection matrix.
 */
@SchemaStyle
@Value.Immutable
public interface CameraOrthographicSchema extends GltfProperty {

    /**
     * The floating-point horizontal magnification of the view. This value **MUST NOT** be equal to zero. This value
     * **SHOULD NOT** be negative. (Required)
     */
    float getXmag();

    /**
     * The floating-point vertical magnification of the view. This value **MUST NOT** be equal to zero. This value
     * **SHOULD NOT** be negative. (Required)
     */
    float getYmag();

    /**
     * The floating-point distance to the far clipping plane. This value **MUST NOT** be equal to zero. {@code zfar}
     * <b>MUST</b> be greater than {@code znear}. (Required)
     */
    float getZfar();

    /**
     * The floating-point distance to the near clipping plane. (Required)
     */
    float getZnear();

}
