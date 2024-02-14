package be.twofold.valen.export.gltf.model.extensions.collections;

import be.twofold.valen.export.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable(copy = false)
public interface EXTCollectionNodeExtensionDef extends Extension {
    List<String> getCollections();
}
