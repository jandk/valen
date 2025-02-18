package be.twofold.valen.format.gltf.model.mesh;

import be.twofold.valen.format.gltf.model.*;
import org.immutables.value.*;

/**
 * A plain JSON object specifying attributes displacements in a morph target, where each key corresponds to one of the
 * three supported attribute semantic ({@code POSITION}, {@code NORMAL}, or {@code TANGENT}) and each value is the index
 * of the accessor containing the attribute displacements' data.
 */
@Schema2Style
@Value.Immutable
public interface MeshPrimitiveTarget {
}
