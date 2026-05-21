package be.twofold.valen.format.gltf.model.asset;

import be.twofold.valen.format.gltf.model.*;
import org.immutables.value.*;

import java.util.*;

/**
 * Metadata about the glTF asset.
 */
@SchemaStyle
@Value.Immutable
public interface AssetSchema extends GltfProperty {

    /**
     * A copyright message suitable for display to credit the content creator.
     */
    Optional<String> getCopyright();

    /**
     * Tool that generated this glTF model.  Useful for debugging.
     */
    Optional<String> getGenerator();

    /**
     * The glTF version in the form of {@code <major>.<minor>} that this asset targets. (Required)
     */
    String getVersion();

    /**
     * The minimum glTF version in the form of {@code <major>.<minor>} that this asset targets. This property **MUST
     * NOT** be greater than the asset version.
     */
    Optional<String> getMinVersion();

}
