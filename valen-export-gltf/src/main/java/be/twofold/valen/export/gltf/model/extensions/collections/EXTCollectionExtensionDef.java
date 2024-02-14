package be.twofold.valen.export.gltf.model.extensions.collections;

import be.twofold.valen.export.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

@SchemaStyle
@Value.Immutable
public interface EXTCollectionExtensionDef extends Extension {
    List<CollectionTreeNode> getCollections();
}
