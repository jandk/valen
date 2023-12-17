package be.twofold.valen.export.gltf.model;

import be.twofold.valen.core.util.*;

public record AssetSchema(
    String generator,
    String version
) {
    public AssetSchema {
        Check.notNull(generator, "generator must not be null");
        Check.notNull(version, "version must not be null");
    }
}
