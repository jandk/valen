package be.twofold.valen.format.gltf.model.accessor;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.bufferview.*;
import org.immutables.value.*;

import java.util.*;

/**
 * An object pointing to a buffer view containing the deviating accessor values. The number of elements is equal to
 * {@code accessor.sparse.count} times number of components. The elements have the same component type as the base
 * accessor. The elements are tightly packed. Data <b>MUST</b> be aligned following the same rules as the base
 * accessor.
 */
@SchemaStyle
@Value.Immutable
public interface AccessorSparseValuesSchema extends GltfProperty {

    /**
     * The index of the bufferView with sparse values. The referenced buffer view **MUST NOT** have its {@code target}
     * or {@code byteStride} properties defined. (Required)
     */
    BufferViewID getBufferView();

    /**
     * The offset relative to the start of the bufferView in bytes.
     */
    Optional<Integer> getByteOffset();

}
