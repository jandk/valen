package be.twofold.valen.format.gltf.model.accessor;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.bufferview.*;
import be.twofold.valen.format.gltf.types.*;
import org.immutables.value.*;

import java.util.*;

/**
 * A typed view into a buffer view that contains raw binary data.
 */
@SchemaStyle
@Value.Immutable
public interface AccessorSchema extends GltfChildOfRootProperty {

    /**
     * The index of the bufferView.
     */
    Optional<BufferViewID> getBufferView();

    /**
     * The offset relative to the start of the buffer view in bytes.
     */
    Optional<Integer> getByteOffset();

    /**
     * The datatype of the accessor's components. (Required)
     */
    AccessorComponentType getComponentType();

    /**
     * Specifies whether integer data values are normalized before usage.
     */
    Optional<Boolean> isNormalized();

    /**
     * The number of elements referenced by this accessor. (Required)
     */
    int getCount();

    /**
     * Specifies if the accessor's elements are scalars, vectors, or matrices. (Required)
     */
    AccessorType getType();

    /**
     * Maximum value of each component in this accessor.
     */
    Optional<Primitive> getMax();

    /**
     * Minimum value of each component in this accessor.
     */
    Optional<Primitive> getMin();

    /**
     * Sparse storage of elements that deviate from their initialization value.
     */
    Optional<AccessorSparseSchema> getSparse();

}
