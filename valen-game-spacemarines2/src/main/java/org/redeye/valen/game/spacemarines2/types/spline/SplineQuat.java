package org.redeye.valen.game.spacemarines2.types.spline;

import be.twofold.valen.core.math.*;

public final class SplineQuat extends SplineData {
    public Quaternion[] quats;

    public SplineQuat(float[] items) {
        quats = new Quaternion[items.length / 5];
        times = new float[items.length / 5];
        for (int i = 0; i < items.length / 5; i++) {
            times[i] = items[i * 5];
            quats[i] = new Quaternion(items[i * 5 + 1], items[i * 5 + 2], items[i * 5 + 3], items[i * 5 + 4]);
        }
    }
}
