package be.twofold.valen.export.gltf.model.extensions.lightspunctual;

import be.twofold.valen.export.gltf.model.extensions.Extension;

import java.util.ArrayList;
import java.util.List;

public class KHRLightsPunctualExtension implements Extension {
    public List<LightSchema> lights = new ArrayList<>();
}
