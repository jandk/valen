package be.twofold.valen.gltf.model;

import org.immutables.value.*;

import java.util.*;

/**
 * A typed view into a buffer view that contains raw binary data.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface AccessorDef extends GltfChildOfRootProperty {
    /**
     * The index of the bufferView.
     */
    Optional<BufferViewId> getBufferView();

    /**
     * The offset relative to the start of the bufferView in bytes.
     */
    OptionalInt getByteOffset();

    /**
     * The datatype of components in the attribute.
     */
    AccessorComponentType getComponentType();

    /**
     * Specifies whether integer data values should be normalized before usage.
     */
    Optional<Boolean> isNormalized();

    /**
     * The number of attributes referenced by this accessor.
     */
    int getCount();

    /**
     * Specifies if the accessor’s elements are scalars, vectors, or matrices.
     */
    AccessorType getType();

    /**
     * Minimum value of each component in this attribute.
     */
    Optional<float[]> getMin();

    /**
     * Maximum value of each component in this attribute.
     */
    Optional<float[]> getMax();
}
