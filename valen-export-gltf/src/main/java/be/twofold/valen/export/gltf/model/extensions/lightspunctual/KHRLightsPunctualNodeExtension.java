package be.twofold.valen.export.gltf.model.extensions.lightspunctual;

import be.twofold.valen.export.gltf.model.extensions.Extension;
import be.twofold.valen.export.gltf.model.extensions.NodeExtension;
import org.immutables.gson.Gson;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.List;

@Value.Immutable
@Gson.TypeAdapters
public interface KHRLightsPunctualNodeExtension extends NodeExtension {
    int light();
}
