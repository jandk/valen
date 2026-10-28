package be.twofold.valen.core.util;

import wtf.reversed.toolbox.math.*;

/**
 * sRGB &lt;-&gt; linear conversion.
 *
 * <h2>Which curve, and why it matters</h2>
 *
 * The transfer function is piecewise: a linear ramp near black meeting a power
 * curve at a threshold. This class uses the C1-continuous constants derived in
 * the Khronos Data Format Specification 1.4, section 13.3.5 -- the ones for which
 * the two segments meet with equal value <em>and</em> equal slope (a true tangent
 * join). With the slope (12.92) and exponent (2.4) fixed, a tangent has a single
 * degree of freedom, so one solved root ({@code LINEAR_THRESHOLD_D}) pins the rest:
 * the offset ({@code a = 0.055011}) and the encoded threshold
 * ({@code 12.92 * root = 0.039293}) all derive from it. See {@code SrgbConstantsTest}.
 *
 * <p>These differ slightly from the literal IEC 61966-2-1 numbers (offset 0.055,
 * threshold 0.04045), which are mutually inconsistent: IEC rounded the upper branch
 * crossing, leaving a 2.33e-9 discontinuity at the join. The two curves differ by
 * at most 4.78e-6 -- 0 of 256 u8 codes decode differently, well inside D3D's
 * 0.6-LSB tolerance -- so correctness does not decide it. The continuous curve is
 * chosen for the exact join (Khronos notes IEC's threshold "may have been
 * incorrectly rounded") and the tighter polynomial fit at the boundary. The IEC
 * constants and their matching coefficients are kept below, commented out.
 *
 * <h2>The fast path</h2>
 *
 * {@link #srgbToLinear} / {@link #linearToSrgb} replace {@link Math#pow} with a
 * degree-5/5 rational polynomial (Horner + {@link Math#fma}), evaluated in pure
 * f32 -- ~6.5x faster than pow. Over an exhaustive f32 sweep of [threshold, 1):
 * max 12 ULP (sRGB-&gt;linear) / 14 ULP (linear-&gt;sRGB), i.e. 0.0002 of a u8 level.
 *
 * <p><b>The f32 path is not strictly monotone.</b> The f32 divide produces
 * millions of adjacent-input reversals, but every one is &lt;= 5 ULP (~0.02 of a
 * u16 code) -- below one quantization step, so they vanish on write to u8/u16 and
 * cannot band. This is safe <em>because the output is quantized</em>, not because
 * the curve is monotone. A consumer using the raw f32 linear values and sensitive
 * to ordering would need an f64-intermediate evaluator (zero reversals, ~4% slower).
 *
 * <p>For byte input the LUTs are exact and faster still, so
 * {@link #srgbByteToLinear} / {@link #linearToSrgbByte} stay table-driven.
 *
 * <h2>Never mix the coefficient sets</h2>
 *
 * Each {@code float[]} coefficient set was fitted to one curve and is worthless
 * against the other: ~12 ULP against its own curve, ~3700 against the other.
 * Swapping the thresholds without swapping the coefficients (or vice versa) is a
 * ~370x accuracy regression that no u8 test will catch -- the constants and the
 * coefficients move together.
 */
public final class Srgb {
    private static final int LUT_BITS = 14; // Keeps us within DirectX spec (< 0.6)
    private static final int LUT_MAX = (1 << LUT_BITS) - 1;
    private static final byte[] TO_SRGB;
    private static final float[] TO_LINEAR;

    private static final double SLOPE_D = 12.92;
    private static final double GAMMA_D = 2.4;
    // Derived constants
    private static final double LINEAR_THRESHOLD_D = 0.003041282560128;
    private static final double SRGB_THRESHOLD_D = SLOPE_D * LINEAR_THRESHOLD_D;
    private static final double OFFSET_D = SLOPE_D * (GAMMA_D - 1) * LINEAR_THRESHOLD_D;
    // IEC constants
    // private static final double SRGB_THRESHOLD_D = 0.04045;
    // private static final double LINEAR_THRESHOLD_D = SRGB_THRESHOLD_D / SLOPE_D;
    // private static final double OFFSET_D = 0.055;

    // Package-private so SrgbConstantsTest can pin them to their exact bits.
    static final float SLOPE = (float) SLOPE_D;
    static final float GAMMA = (float) GAMMA_D;
    static final float OFFSET = (float) OFFSET_D;
    static final float LINEAR_THRESHOLD = (float) LINEAR_THRESHOLD_D;
    static final float SRGB_THRESHOLD = (float) SRGB_THRESHOLD_D;
    static final float GAIN = (float) (1.0 + OFFSET_D);
    static final float INV_GAMMA = (float) (1.0 / GAMMA_D);
    static final float LINEAR_SCALE = (float) (1.0 / SLOPE_D);

    private static final float[] L2S_P = {-1.3564410e-2f, +9.3368962e-2f, +1.1572705e+1f, +5.0021248e+1f, +2.7924210e+1f};
    private static final float[] L2S_Q = {+2.6338127e-1f, +8.9989176e+0f, +4.4777397e+1f, +3.4558308e+1f, +1.0000000e+0f};
    private static final float[] S2L_P = {+1.6728146e-2f, +8.0895364e-1f, +1.2884397e+1f, +6.8543747e+1f, +8.2246254e+1f};
    private static final float[] S2L_Q = {+2.0038078e+1f, +9.6814682e+1f, +5.3773033e+1f, -7.1256380e+0f, +1.0000000e+0f};

    static {
        int size = 1 << LUT_BITS;
        float sizeMinus1 = size - 1;

        TO_SRGB = new byte[size];
        for (int i = 0; i < size; i++) {
            TO_SRGB[i] = FloatMath.packUNorm8(linearToSrgbExact(i / sizeMinus1));
        }

        TO_LINEAR = new float[256];
        for (int i = 0; i < 256; i++) {
            TO_LINEAR[i] = srgbToLinearExact(FloatMath.unpackUNorm8((byte) i));
        }
    }

    private Srgb() {
    }

    /**
     * Converts a linear-light float to sRGB encoding using the fast
     * rational-polynomial approximation. Inputs outside {@code [0, 1]} are clamped
     * to {@code 0} or {@code 1}. Accurate to ~14 ULP; see {@link #linearToSrgbExact}
     * for the exact {@link Math#pow} reference, or {@link #linearToSrgbByte} for
     * byte output.
     */
    public static float linearToSrgb(float linear) {
        if (linear < 0.0) {
            return 0.0f;
        }
        if (linear >= 1.0) {
            return 1.0f;
        }
        if (linear <= LINEAR_THRESHOLD) {
            return linear * SLOPE;
        }
        float s = FloatMath.sqrt(linear);
        return evalRationalPoly5(s, L2S_P, L2S_Q);
    }

    /**
     * Converts an sRGB-encoded float to linear light using the fast
     * rational-polynomial approximation. Inputs outside {@code [0, 1]} are clamped
     * to {@code 0} or {@code 1}. Accurate to ~12 ULP; see {@link #srgbToLinearExact}
     * for the exact {@link Math#pow} reference, or {@link #srgbByteToLinear} for
     * byte input.
     */
    public static float srgbToLinear(float gamma) {
        if (gamma < 0.0) {
            return 0.0f;
        }
        if (gamma >= 1.0) {
            return 1.0f;
        }
        if (gamma <= SRGB_THRESHOLD) {
            return gamma * LINEAR_SCALE;
        }
        return evalRationalPoly5(gamma, S2L_P, S2L_Q);
    }

    /**
     * Converts a linear float value to sRGB. Input should be in {@code [0, 1]}.
     */
    public static float linearToSrgbExact(float f) {
        if (f <= LINEAR_THRESHOLD) {
            return f * SLOPE;
        } else {
            return Math.fma((float) Math.pow(f, INV_GAMMA), GAIN, -OFFSET);
        }
    }

    /**
     * Converts an sRGB float value to linear. Input should be in {@code [0, 1]}.
     */
    public static float srgbToLinearExact(float f) {
        if (f <= SRGB_THRESHOLD) {
            return f * (1.0f / SLOPE);
        } else {
            float a = Math.fma(f, 1.0f / GAIN, OFFSET / GAIN);
            return (float) Math.pow(a, GAMMA);
        }
    }

    /**
     * Converts a linear float value to an sRGB-encoded byte using a LUT.
     * Values outside {@code [0, 1]} are clamped to 0 or 255.
     */
    public static byte linearToSrgbByte(float f) {
        if (f >= 0.0f && f <= 1.0f) {
            return TO_SRGB[(int) Math.fma(f, LUT_MAX, 0.5f)];
        } else if (f > 1.0f) {
            return (byte) 255;
        } else {
            return 0;
        }
    }

    /**
     * Converts an sRGB-encoded byte to a linear float value using a LUT.
     */
    public static float srgbByteToLinear(byte b) {
        return TO_LINEAR[Byte.toUnsignedInt(b)];
    }

    private static float evalRationalPoly5(float x, float[] p, float[] q) {
        float yp = p[4];
        yp = Math.fma(yp, x, p[3]);
        yp = Math.fma(yp, x, p[2]);
        yp = Math.fma(yp, x, p[1]);
        yp = Math.fma(yp, x, p[0]);

        float yq = q[4];
        yq = Math.fma(yq, x, q[3]);
        yq = Math.fma(yq, x, q[2]);
        yq = Math.fma(yq, x, q[1]);
        yq = Math.fma(yq, x, q[0]);

        return yp / yq;
    }
}
