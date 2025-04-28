package be.twofold.valen.game.greatcircle.reader.decl.material2;

import be.twofold.valen.game.greatcircle.defines.*;
import be.twofold.valen.game.idtech.defines.*;

public record MaterialImageOpts(
    TextureType type,
    TextureFilter filter,
    TextureRepeat repeat,
    TextureFormat format,
    short atlasPadding,
    int minMip,
    boolean fullScaleBias,
    boolean noMips,
    boolean fftBloom
) {
}
