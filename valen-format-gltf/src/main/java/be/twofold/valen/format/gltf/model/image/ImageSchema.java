package be.twofold.valen.format.gltf.model.image;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.bufferview.*;
import org.immutables.value.*;

import java.net.*;
import java.util.*;

/**
 * Image data used to create a texture. Image <b>MAY</b> be referenced by an URI (or IRI) or a buffer view index.
 */
@Schema2Style
@Value.Immutable
public interface ImageSchema extends GltfChildOfRootProperty {

    /**
     * The URI (or IRI) of the image.
     */
    Optional<URI> getUri();

    /**
     * The image's media type. This field <b>MUST</b> be defined when {@code bufferView} is defined.
     */
    Optional<ImageMimeType> getMimeType();

    /**
     * The index of the bufferView that contains the image. This field **MUST NOT** be defined when {@code uri} is
     * defined.
     */
    Optional<BufferViewID> getBufferView();

}
