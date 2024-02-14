package be.twofold.valen.export.gltf.model.extensions.lightspunctual;

import be.twofold.valen.export.gltf.model.Extension;
import be.twofold.valen.export.gltf.model.SchemaStyle;
import org.immutables.value.Value;

@SchemaStyle
@Value.Immutable(copy = false)
public interface KHRLightsPunctualNodeExtensionDef extends Extension {
    int getLight();
}
