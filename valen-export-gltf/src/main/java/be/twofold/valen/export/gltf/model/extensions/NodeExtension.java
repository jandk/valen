package be.twofold.valen.export.gltf.model.extensions;

import be.twofold.valen.export.gltf.model.extensions.lightspunctual.KHRLightsPunctualNodeExtension;
import org.immutables.gson.Gson;

@Gson.ExpectedSubtypes({KHRLightsPunctualNodeExtension.class})
public interface NodeExtension {
}

