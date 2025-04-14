package be.twofold.valen.game.greatcircle.reader.decl.material2;

import be.twofold.valen.game.greatcircle.reader.image.*;

public record MaterialImageOpts(
    ImageTextureType type,
    ImageTextureFilter filter,
    ImageTextureRepeat repeat,
    ImageTextureFormat format,
    short atlasPadding,
    int minMip,
    boolean fullScaleBias,
    boolean noMips,
    boolean fftBloom
) {
}
