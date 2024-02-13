package be.twofold.valen.export.gltf.model.extensions.lightspunctual;

import be.twofold.valen.core.math.Vector3;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Gson.TypeAdapters
public interface SpotLightSchema extends LightSchema{

   Optional<Float> outerConeAngle();
   Optional<Float> innerConeAngle();
}
