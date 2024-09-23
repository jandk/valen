package be.twofold.valen.gltf.model;

import org.immutables.value.*;

import java.net.*;
import java.util.*;

/**
 * Image data used to create a texture. Image MAY be referenced by an URI (or IRI) or a buffer view index.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface ImageDef extends GltfChildOfRootProperty {

    /**
     * The URI (or IRI) of the image.
     */
    Optional<URI> getUri();

    /**
     * The image’s media type. This field MUST be defined when bufferView is defined.
     */
    Optional<MimeType> getMimeType();

    /**
     * The index of the bufferView that contains the image. This field MUST NOT be defined when uri is defined.
     */
    Optional<BufferViewId> getBufferView();

}
