package be.twofold.valen.game.doom.vmtr;

import be.twofold.valen.core.texture.*;

/**
 * The layers a virtual texture page carries, and which slot of the page header holds each.
 */
public enum VmtrLayer {
    DIFFUSE("_c", 0, TextureFormat.R8G8B8A8_SRGB),
    SPECULAR("_s", 1, TextureFormat.R8G8B8A8_SRGB),
    LIGHTMAP("_g", 2, TextureFormat.R8G8B8A8_UNORM),
    COLORMASK("_m", 3, TextureFormat.BC7_UNORM),
    COVER("_cover", 4, TextureFormat.R8_UNORM);

    private final String suffix;
    private final int slot;
    private final TextureFormat format;

    VmtrLayer(String suffix, int slot, TextureFormat format) {
        this.suffix = suffix;
        this.slot = slot;
        this.format = format;
    }

    public String suffix() {
        return suffix;
    }

    public int slot() {
        return slot;
    }

    public TextureFormat format() {
        return format;
    }
}
