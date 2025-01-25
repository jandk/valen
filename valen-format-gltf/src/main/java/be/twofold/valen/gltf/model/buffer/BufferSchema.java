package be.twofold.valen.gltf.model.buffer;

import be.twofold.valen.gltf.model.*;
import org.immutables.value.*;

import java.net.*;
import java.util.*;

/**
 * A buffer points to binary geometry, animation, or skins.
 */
@Schema2Style
@Value.Immutable
public interface BufferSchema extends GltfChildOfRootProperty {

    /**
     * The URI (or IRI) of the buffer.
     */
    Optional<URI> getUri();

    /**
     * The length of the buffer in bytes. (Required)
     */
    int getByteLength();

}
