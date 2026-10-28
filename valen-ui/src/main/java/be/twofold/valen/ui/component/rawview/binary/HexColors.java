package be.twofold.valen.ui.component.rawview.binary;

import be.twofold.valen.core.util.*;
import javafx.scene.paint.*;
import wtf.reversed.toolbox.math.*;

/**
 * I didn't invent these unfortunately.
 * <p>
 * Check out: <a href="https://simonomi.dev/blog/color-code-your-bytes/">simonomi.dev</a>
 */
final class HexColors {
    private static final Matrix3 OKLAB_TO_LMS = new Matrix3(
        +1.0000000000000000f, +1.0000000000000000f, +1.0000000000000000f,
        +0.3963377773761749f, -0.1055613458156586f, -0.0894841775298119f,
        +0.2158037573099136f, -0.0638541728258133f, -1.2914855480194092f
    );

    private static final Matrix3 LMS_TO_XYZ = new Matrix3(
        +1.2268798758459243f, -0.0405757452148008f, -0.0763729366746601f,
        -0.5578149944602171f, +1.1122868032803170f, -0.4214933324022432f,
        +0.2813910456659647f, -0.0717110580655164f, +1.5869240198367816f
    );

    private static final Matrix3 XYZ_TO_RGB = new Matrix3(
        +3.2409699419045213f, -0.9692436362808798f, +0.0556300796969936f,
        -1.5373831775700935f, +1.8759675015077206f, -0.2039769588889766f,
        -0.4986107602930033f, +0.0415550574071756f, +1.0569715142428786f
    );


    private static final Color[] COLORS;
    private static final Color BYTE_00 = Color.GRAY;
    private static final Color BYTE_FF = Color.WHITE;
    private static final Color GUTTER = Color.GRAY;
    private static final Color RED = Color.rgb(0xFC, 0x6A, 0x5D);
    private static final Color GREEN = Color.rgb(0x50, 0xFA, 0x7B);
    private static final Color YELLOW = Color.rgb(0xF1, 0xFA, 0x8C);


    static {
        COLORS = new Color[16];
        for (int i = 0; i < COLORS.length; i++) {
            COLORS[i] = fromOkLch(0.75f, 0.18f, i * (360f / COLORS.length));
        }
    }

    private HexColors() {
    }

    private static Color fromOkLch(float l, float c, float h) {
        // OKLch -> OKLab
        float hRad = FloatMath.toRadians(h);
        var okLab = new Vector3(
            l,
            c * FloatMath.cos(hRad),
            c * FloatMath.sin(hRad)
        );

        // OKLab -> XYZ
        var lms = okLab.transform(OKLAB_TO_LMS);
        lms = lms.multiply(lms).multiply(lms);
        var xyz = lms.transform(LMS_TO_XYZ);

        // XYZ -> RGB
        var rgb = xyz.transform(XYZ_TO_RGB);

        // RGB -> SRGB
        return new Color(
            Math.clamp(Srgb.linearToSrgb(rgb.x()), 0.0f, 1.0f),
            Math.clamp(Srgb.linearToSrgb(rgb.y()), 0.0f, 1.0f),
            Math.clamp(Srgb.linearToSrgb(rgb.z()), 0.0f, 1.0f),
            1.0f
        );
    }

    static Color gutter() {
        return GUTTER;
    }

    static Color forByte(int value) {
        return switch (value) {
            case 0x00 -> BYTE_00;
            case 0xFF -> BYTE_FF;
            default -> COLORS[(value >>> 4) & 0x0F];
        };
    }

    static Color forChar(int value) {
        return switch (value) {
            case 0x00 -> BYTE_00;
            case 0xFF -> BYTE_FF;
            default -> {
                if (Ascii.isGraph((char) value) || Ascii.isSpace((char) value)) {
                    yield RED;
                }
                if (Ascii.isAscii((char) value)) {
                    yield GREEN;
                }
                yield YELLOW;
            }
        };
    }

}
