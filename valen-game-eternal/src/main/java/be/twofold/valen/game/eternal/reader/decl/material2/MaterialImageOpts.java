package be.twofold.valen.game.eternal.reader.decl.material2;

import be.twofold.valen.game.eternal.reader.image.*;

public record MaterialImageOpts(
    ImageTextureType type,
    ImageTextureFilter filter,
    ImageTextureRepeat repeat,
    ImageTextureFormat format,
    Short atlasPadding,
    Integer minMip,
    boolean fullScaleBias,
    boolean noMips,
    boolean fftBloom
) {
}
