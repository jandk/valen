package be.twofold.valen.export.gltf.model.extensions.lightspunctual;

import be.twofold.valen.export.gltf.model.*;
import org.immutables.value.*;

import java.util.List;

@SchemaStyle
@Value.Immutable
public interface KHRLightsPunctualExtensionDef extends Extension {
    List<LightSchema> getLights();
}
