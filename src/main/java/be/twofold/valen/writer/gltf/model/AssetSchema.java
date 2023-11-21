package be.twofold.valen.writer.gltf.model;

import java.util.*;

public record AssetSchema(
    String generator,
    String version
) {
    public AssetSchema {
        Objects.requireNonNull(generator, "generator must not be null");
        Objects.requireNonNull(version, "version must not be null");
    }
}
