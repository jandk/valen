package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.image.*;

public final class GltfTextureMapper {
    private final GltfContext context;

    public GltfTextureMapper(GltfContext context) {
        this.context = context;
    }

    public ImageSchema map(Texture texture) {
        return null;
    }
}
