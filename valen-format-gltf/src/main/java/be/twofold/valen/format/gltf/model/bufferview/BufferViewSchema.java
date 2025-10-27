package be.twofold.valen.format.gltf.model.bufferview;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.buffer.*;
import org.immutables.value.*;

import java.util.*;

/**
 * A view into a buffer generally representing a subset of the buffer.
 */
@SchemaStyle
@Value.Immutable
public interface BufferViewSchema extends GltfChildOfRootProperty {

    /**
     * The index of the buffer. (Required)
     */
    BufferID getBuffer();

    /**
     * The offset into the buffer in bytes.
     */
    Optional<Integer> getByteOffset();

    /**
     * The length of the bufferView in bytes. (Required)
     */
    int getByteLength();

    /**
     * The stride, in bytes.
     */
    Optional<Integer> getByteStride();

    /**
     * The hint representing the intended GPU buffer type to use with this buffer view.
     */
    Optional<BufferViewTarget> getTarget();

}
