package be.twofold.valen.export.gltf.model.extensions.lightspunctual;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
@Gson.TypeAdapters
public interface PointLightSchema extends LightSchema {

}