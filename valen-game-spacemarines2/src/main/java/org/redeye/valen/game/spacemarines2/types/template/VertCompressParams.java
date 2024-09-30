package org.redeye.valen.game.spacemarines2.types.template;

import java.util.*;

public class VertCompressParams {
    public short[] offset = new short[3];
    public short[] scale = new short[3]; // Unsigned


    float unpackOffset(short offset) {
        float a = (short) (offset ^ 0x8000);
        float b = a - 32768.0f;
        return offset;
    }

    float[] unpack() {
        var data = new float[6];

        return data;
    }

    @Override
    public String toString() {
        return "VertCompressParams{" +
            "offset=" + Arrays.toString(offset) +
            ", scale=" + Arrays.toString(scale) +
            '}';
    }
}
