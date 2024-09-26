package org.redeye.valen.game.spacemarines2.types.spline;

public final class SplineLinear1D extends SplineData {
    public float[] pos;

    public SplineLinear1D(float[] items) {
        pos = new float[items.length / 2];
        times = new float[items.length / 2];
        for (int i = 0; i < items.length / 2; i++) {
            times[i] = items[i * 2];
            pos[i] = items[i * 2 + 1];
        }
    }
}
