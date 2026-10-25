package be.twofold.valen.core.util;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

class SrgbConstantsTest {
    private static final double SLOPE_D = 12.92;
    private static final double GAMMA_D = 2.4;
    private static final double LINEAR_THRESHOLD_D = 0.003041282560128;
    private static final double SRGB_THRESHOLD_D = SLOPE_D * LINEAR_THRESHOLD_D;
    private static final double OFFSET_D = SLOPE_D * (GAMMA_D - 1) * LINEAR_THRESHOLD_D;

    @Test
    void constantsDeriveToTheirExactShippedBits() {
        assertBits("SLOPE", Srgb.SLOPE, (float) SLOPE_D);
        assertBits("GAMMA", Srgb.GAMMA, (float) GAMMA_D);
        assertBits("OFFSET", Srgb.OFFSET, (float) OFFSET_D);
        assertBits("GAIN", Srgb.GAIN, (float) (1.0 + OFFSET_D));
        assertBits("INV_GAMMA", Srgb.INV_GAMMA, (float) (1.0 / GAMMA_D));
        assertBits("LINEAR_THRESHOLD", Srgb.LINEAR_THRESHOLD, (float) LINEAR_THRESHOLD_D);
        assertBits("SRGB_THRESHOLD", Srgb.SRGB_THRESHOLD, (float) SRGB_THRESHOLD_D);
        assertBits("LINEAR_SCALE", Srgb.LINEAR_SCALE, (float) (1.0 / SLOPE_D));
    }

    @Test
    void encodedThresholdIsSlopeTimesLinearThresholdInF32() {
        assertThat(Srgb.SLOPE * Srgb.LINEAR_THRESHOLD)
            .isEqualTo(Srgb.SRGB_THRESHOLD);
    }

    private static void assertBits(String name, float actual, float expected) {
        assertThat(actual)
            .withFailMessage("Srgb.%s = %s but the derivation gives %s", name, actual, expected)
            .isEqualTo(expected);
    }
}
