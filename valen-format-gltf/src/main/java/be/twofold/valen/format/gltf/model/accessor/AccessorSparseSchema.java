package be.twofold.valen.format.gltf.model.accessor;

import be.twofold.valen.format.gltf.model.*;
import org.immutables.value.*;

/**
 * Sparse storage of accessor values that deviate from their initialization value.
 */
@SchemaStyle
@Value.Immutable
public interface AccessorSparseSchema extends GltfProperty {

    /**
     * Number of deviating accessor values stored in the sparse array. (Required)
     */
    int getCount();

    /**
     * An object pointing to a buffer view containing the indices of deviating accessor values. The number of indices is
     * equal to {@code count}. Indices <b>MUST</b> strictly increase. (Required)
     */
    AccessorSparseIndicesSchema getIndices();

    /**
     * An object pointing to a buffer view containing the deviating accessor values. (Required)
     */
    AccessorSparseValuesSchema getValues();

}
