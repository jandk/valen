package be.twofold.valen.export.gltf.model.extensions.lightspunctual;

import be.twofold.valen.export.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface SpotDef {
    Optional<Float> getOuterConeAngle();

    Optional<Float> getInnerConeAngle();
}
