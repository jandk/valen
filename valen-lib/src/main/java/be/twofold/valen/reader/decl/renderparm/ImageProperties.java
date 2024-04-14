package be.twofold.valen.reader.decl.renderparm;

import be.twofold.valen.reader.image.*;

final class ImageProperties {
    ImageTextureType type = ImageTextureType.TT_2D;
    ImageTextureFormat format = ImageTextureFormat.FMT_NONE;
    int padding;
    boolean fullScaleBias;
    boolean fftBloom;
    boolean noMips;
}
