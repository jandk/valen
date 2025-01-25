package be.twofold.valen.format.gltf.model.camera;

import be.twofold.valen.format.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

/**
 * A perspective camera containing properties to create a perspective projection matrix.
 */
@Schema2Style
@Value.Immutable
public interface CameraPerspectiveSchema extends GltfProperty {

    /**
     * The floating-point aspect ratio of the field of view.
     */
    Optional<Float> getAspectRatio();

    /**
     * The floating-point vertical field of view in radians. This value <b>SHOULD</b> be less than π. (Required)
     */
    float getYfov();

    /**
     * The floating-point distance to the far clipping plane.
     */
    Optional<Float> getZfar();

    /**
     * The floating-point distance to the near clipping plane. (Required)
     */
    float getZnear();

}
