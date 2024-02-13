package be.twofold.valen.export.gltf.model;

import org.immutables.value.*;

import java.util.*;

/**
 * Metadata about the glTF asset.
 */
@SchemaStyle
@Value.Immutable(copy = false)
public interface AssetDef extends PropertyDef {
    /**
     * A copyright message suitable for display to credit the content creator.
     */
    Optional<String> getCopyright();

    /**
     * Tool that generated this glTF model. Useful for debugging.
     */
    Optional<String> getGenerator();

    /**
     * The glTF version that this asset targets.
     */
    String getVersion();

    /**
     * The minimum glTF version that this asset targets.
     */
    Optional<String> getMinVersion();
}
