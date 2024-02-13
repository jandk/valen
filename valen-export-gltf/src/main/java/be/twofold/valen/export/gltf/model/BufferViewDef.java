package be.twofold.valen.export.gltf.model;

import org.immutables.value.*;

import java.util.*;

/**
 * A bufferView is a view into a buffer generally representing a subset of the buffer.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface BufferViewDef extends GltfChildOfRootProperty {
    /**
     * The index of the buffer.
     */
    BufferId getBuffer();

    /**
     * The length of the bufferView in bytes.
     */
    int getByteLength();

    /**
     * The offset into the buffer in bytes.
     */
    OptionalInt getByteOffset();

    /**
     * The stride, in bytes.
     */
    OptionalInt getByteStride();

    /**
     * The hint representing the intended GPU buffer type to use with this buffer view.
     */
    BufferViewTarget getTarget();
}
