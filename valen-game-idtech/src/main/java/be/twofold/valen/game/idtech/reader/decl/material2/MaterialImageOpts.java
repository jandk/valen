package be.twofold.valen.game.idtech.reader.decl.material2;

import be.twofold.valen.game.idtech.reader.image.*;

public record MaterialImageOpts(
    TextureType type,
    TextureFilter filter,
    TextureRepeat repeat,
    TextureFormat format,
    Short atlasPadding,
    Integer minMip,
    boolean fullScaleBias,
    boolean noMips,
    boolean fftBloom
) {
}
