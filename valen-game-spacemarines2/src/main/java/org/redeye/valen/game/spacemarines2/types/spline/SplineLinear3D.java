package org.redeye.valen.game.spacemarines2.types.spline;

import be.twofold.valen.core.math.*;

public final class SplineLinear3D extends SplineData {
    public Vector3[] pos;

    public SplineLinear3D(float[] items) {
        pos = new Vector3[items.length / 4];
        times = new float[items.length / 4];
        for (int i = 0; i < items.length / 4; i++) {
            times[i] = items[i * 4];
            pos[i] = new Vector3(items[i * 4 + 1], items[i * 4 + 2], items[i * 4 + 3]);
        }
    }
}
