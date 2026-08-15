package be.twofold.valen.game.doom.vmtr;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.doom.megatexture.mega2.*;

/**
 * The layers a virtual texture page carries, in the order {@code Mega2PageHeader} stores them.
 */
public enum VmtrLayer {
    DIFFUSE("_c", TextureFormat.R8G8B8A8_SRGB),
    SPECULAR("_s", TextureFormat.R8G8B8A8_SRGB),
    LIGHTMAP("_g", TextureFormat.R8G8B8A8_UNORM),
    COLORMASK("_m", TextureFormat.BC7_UNORM);

    private final String suffix;
    private final TextureFormat format;

    VmtrLayer(String suffix, TextureFormat format) {
        this.suffix = suffix;
        this.format = format;
    }

    public String suffix() {
        return suffix;
    }

    public TextureFormat format() {
        return format;
    }

    /**
     * Returns {@code 0} when the page does not carry this layer.
     */
    public int sizeIn(Mega2PageHeader header) {
        return Short.toUnsignedInt(switch (this) {
            case DIFFUSE -> header.diffuseSize();
            case SPECULAR -> header.specularSize();
            case LIGHTMAP -> header.lightmapSize();
            case COLORMASK -> header.colormaskSize();
        });
    }

    /**
     * How far past the page header this layer starts. Layers are stored back to back.
     */
    public int offsetIn(Mega2PageHeader header) {
        var offset = 0;
        for (var layer : values()) {
            if (layer == this) {
                return offset;
            }
            offset += layer.sizeIn(header);
        }
        throw new IllegalStateException();
    }

}
