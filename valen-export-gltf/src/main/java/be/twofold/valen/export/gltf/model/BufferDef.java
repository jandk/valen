package be.twofold.valen.export.gltf.model;

import org.immutables.value.*;

import java.net.*;
import java.util.*;

/**
 * A buffer points to binary geometry, animation, or skins.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface BufferDef extends GltfChildOfRootProperty {
    /**
     * The length of the buffer in bytes.
     */
    int getByteLength();

    /**
     * The uri of the buffer.
     */
    Optional<URI> getUri();
}
