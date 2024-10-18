package org.redeye.valen.game.spacemarines2.types.template;

import be.twofold.valen.core.math.*;

public record VertCompressParams(
    Vector3 offset,
    Vector3 scale // Unsigned
) {

    VertCompressParams() {
        this(Vector3.Zero, Vector3.Zero);
    }
}
