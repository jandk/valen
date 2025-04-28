package be.twofold.valen.game.eternal.reader.decl.material2;

import be.twofold.valen.game.eternal.defines.*;
import be.twofold.valen.game.idtech.defines.*;

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
