package be.twofold.valen.export.gltf.model.extensions.lightspunctual;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Optional;

@Value.Immutable
@Gson.TypeAdapters
public interface LightSchema {

    Optional<String> name();

    Optional<ArrayList<Float>> color();

    Optional<Float> intensity();

    LightType type();

    Optional<Float> range();
}
