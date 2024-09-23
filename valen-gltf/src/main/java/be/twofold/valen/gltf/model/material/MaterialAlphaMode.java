package be.twofold.valen.gltf.model.material;

/**
 * The material’s alpha rendering mode enumeration specifying the interpretation of the alpha value of the base color.
 */
public enum MaterialAlphaMode {
    /**
     * The alpha value is ignored, and the rendered output is fully opaque.
     */
    OPAQUE,
    /**
     * The rendered output is either fully opaque or fully transparent depending on the alpha value and the specified `alphaCutoff` value;
     * the exact appearance of the edges **MAY** be subject to implementation-specific techniques such as \"`Alpha-to-Coverage`\".
     */
    MASK,
    /**
     * The alpha value is used to composite the source and destination areas.
     * The rendered output is combined with the background using the normal painting operation (i.e. the Porter and Duff over operator).
     */
    BLEND,
}
