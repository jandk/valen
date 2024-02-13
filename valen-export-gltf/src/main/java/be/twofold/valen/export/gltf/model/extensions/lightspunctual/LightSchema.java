package be.twofold.valen.export.gltf.model.extensions.lightspunctual;

import be.twofold.valen.core.math.Vector3;
import java.util.Optional;

public interface LightSchema {

    Optional<String> name();

    Optional<Vector3> color();

    Optional<Float> intensity();

    LightType type();

    Optional<Float> range();
}
