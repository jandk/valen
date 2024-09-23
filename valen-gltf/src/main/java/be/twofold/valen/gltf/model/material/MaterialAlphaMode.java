package be.twofold.valen.gltf.model.material;

import be.twofold.valen.gltf.model.*;

/**
 * The material’s alpha rendering mode enumeration specifying the interpretation of the alpha value of the base color.
 */
public enum MaterialAlphaMode implements ValueEnum<String> {
    /**
     * The alpha value is ignored, and the rendered output is fully opaque.
     */
    OPAQUE("OPAQUE"),
    /**
     * The rendered output is either fully opaque or fully transparent depending on the alpha value and the specified `alphaCutoff` value;
     * the exact appearance of the edges **MAY** be subject to implementation-specific techniques such as \"`Alpha-to-Coverage`\".
     */
    MASK("MASK"),
    /**
     * The alpha value is used to composite the source and destination areas.
     * The rendered output is combined with the background using the normal painting operation (i.e. the Porter and Duff over operator).
     */
    BLEND("BLEND");

    private final String value;

    MaterialAlphaMode(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
