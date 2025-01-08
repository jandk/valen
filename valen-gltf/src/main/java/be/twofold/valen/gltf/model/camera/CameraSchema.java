package be.twofold.valen.gltf.model.camera;

import be.twofold.valen.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

/**
 * A camera's projection.  A node <b>MAY</b> reference a camera to apply a transform to place the camera in the scene.
 */
@Schema2Style
@Value.Immutable
public interface CameraSchema extends GltfChildOfRootProperty {

    /**
     * An orthographic camera containing properties to create an orthographic projection matrix. This property **MUST
     * NOT** be defined when {@code perspective} is defined.
     */
    Optional<CameraOrthographicSchema> getOrthographic();

    /**
     * A perspective camera containing properties to create a perspective projection matrix. This property **MUST NOT**
     * be defined when {@code orthographic} is defined.
     */
    Optional<CameraPerspectiveSchema> getPerspective();

    /**
     * Specifies if the camera uses a perspective or orthographic projection. (Required)
     */
    CameraType getType();

}
