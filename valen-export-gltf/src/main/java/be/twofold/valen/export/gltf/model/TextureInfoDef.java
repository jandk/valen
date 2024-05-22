package be.twofold.valen.export.gltf.model;

import org.immutables.value.*;

import java.util.*;


@SchemaStyle
@Value.Immutable(copy = false)
public interface TextureInfoDef extends GltfChildOfRootProperty {
    TextureId getIndex();

    Optional<Integer> getTexCoord();
}
