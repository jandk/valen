package be.twofold.valen.format.gltf.model.accessor;

import be.twofold.valen.format.gltf.model.*;
import be.twofold.valen.format.gltf.model.bufferview.*;
import org.immutables.value.*;

import java.util.*;

/**
 * An object pointing to a buffer view containing the indices of deviating accessor values. The number of indices is
 * equal to {@code accessor.sparse.count}. Indices <b>MUST</b> strictly increase.
 */
@Schema2Style
@Value.Immutable
public interface AccessorSparseIndicesSchema extends GltfProperty {

    /**
     * The index of the buffer view with sparse indices. The referenced buffer view **MUST NOT** have its {@code target}
     * or {@code byteStride} properties defined. The buffer view and the optional {@code byteOffset} <b>MUST</b> be
     * aligned to the {@code componentType} byte length. (Required)
     */
    BufferViewID getBufferView();

    /**
     * The offset relative to the start of the buffer view in bytes.
     */
    Optional<Integer> getByteOffset();

    /**
     * The indices data type. (Required)
     */
    AccessorComponentType getComponentType();

}
