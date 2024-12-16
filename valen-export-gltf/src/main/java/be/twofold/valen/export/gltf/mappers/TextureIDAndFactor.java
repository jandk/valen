package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.math.*;
import be.twofold.valen.gltf.model.texture.*;

public record TextureIDAndFactor(
    TextureID textureID,
    Vector4 factor
) {
}
