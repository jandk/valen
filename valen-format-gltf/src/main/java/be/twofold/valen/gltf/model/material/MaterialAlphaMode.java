package be.twofold.valen.gltf.model.material;

import be.twofold.valen.gltf.model.*;

public enum MaterialAlphaMode implements SerializableEnum<String> {
    /**
     * The alpha value is ignored, and the rendered output is fully opaque.
     */
    OPAQUE,
    /**
     * The rendered output is either fully opaque or fully transparent depending on the alpha value and the specified
     * {@code alphaCutoff} value; the exact appearance of the edges <b>MAY</b> be subject to implementation-specific
     * techniques such as \"{@code Alpha-to-Coverage}\".
     */
    MASK,
    /**
     * The alpha value is used to composite the source and destination areas. The rendered output is combined with the
     * background using the normal painting operation (i.e. the Porter and Duff over operator).
     */
    BLEND;

    @Override
    public String value() {
        return name();
    }
}
