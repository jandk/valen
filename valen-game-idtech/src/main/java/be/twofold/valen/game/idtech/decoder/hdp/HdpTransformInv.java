package be.twofold.valen.game.idtech.decoder.hdp;

/**
 * Inverse 4x4 lapped transform: the two IDCT stages plus the overlap filter that straddles macroblock boundaries.
 *
 * <p>Scalar where the engine is SSE2; the rotations are the scalar equivalent of its byte-split form, identical
 * because the high half it scales separately is always divisible by the shift. The helpers named {@code invOdd},
 * {@code strHSTdec} and friends keep reference-implementation names - the engine inlines them and has none.
 */
final class HdpTransformInv {

    private HdpTransformInv() {
    }

    // DEVIATION: hdpref's OL_TWO, post-process, thumbnail and bHPAbsent branches are all unreachable for id-Tech
    // and are not ported.
    static void invTransformMacroblock(
        int[] rowBufs,
        int numChannels, int channelStride,
        int prevRowBase, int currRowBase,
        int mbX, int mbWidth,
        int mbY, int mbHeight
    ) {
        boolean left = (mbX == 0);
        boolean right = (mbX == mbWidth);
        boolean top = (mbY == 0);
        boolean bottom = (mbY == mbHeight);

        int[] p = rowBufs;
        for (int ch = 0; ch < numChannels; ch++) {
            int chBase = ch * channelStride;
            int p0 = chBase + prevRowBase + mbX * 256;
            int p1 = chBase + currRowBase + mbX * 256;

            // DEVIATION: hdpref normalizes here; id-Tech pre-doubles chroma DC through the QP instead, so there is
            // nothing to do.
            if (!bottom && !right) {
                invTransform4x4Stage2(p, p1);
            }

            if (!top) {
                for (int j = (left ? 32 : -96); j < (right ? 32 : 160); j += 64) {
                    invTransform4x4Stage1(p, p0 + j);
                    invTransform4x4Stage1(p, p0 + j + 16);
                }
            }
            if (!bottom) {
                for (int j = (left ? 0 : -128); j < (right ? 0 : 128); j += 64) {
                    invTransform4x4Stage1(p, p1 + j);
                    invTransform4x4Stage1(p, p1 + j + 16);
                }
            }

            // DEVIATION: hdpref has no filter at the macroblock-grid corners; id-Tech adds this one.
            if (top && left) {
                postFilter2x2Stage1(p, p1);
            } else if (top && right) {
                postFilter2x2Stage1(p, p1 - 60);
            } else if (bottom && left) {
                postFilter2x2Stage1(p, p0 + 56);
            } else if (bottom && right) {
                postFilter2x2Stage1(p, p0 - 4);
            }

            if (left || right) {
                int j = left ? 10 : -50;
                if (!top) {
                    int q = p0 + 16 + j;
                    postFilter2x2Stage1(p, q/* */, q - +2, q + +6, q + +8);
                    postFilter2x2Stage1(p, q + +1, q - +1, q + +7, q + +9);
                    postFilter2x2Stage1(p, q + 16, q + 14, q + 22, q + 24);
                    postFilter2x2Stage1(p, q + 17, q + 15, q + 23, q + 25);
                }
                if (!bottom) {
                    int q = p1 + j;
                    postFilter2x2Stage1(p, q/**/, q - 2, q + 6, q + 8);
                    postFilter2x2Stage1(p, q + 1, q - 1, q + 7, q + 9);
                }
                if (!top && !bottom) {
                    postFilter2x2Stage1(p, p0 + 48 + j/**/, p0 + 48 + j - 2, p1 - 10 + j, p1 - 8 + j);
                    postFilter2x2Stage1(p, p0 + 48 + j + 1, p0 + 48 + j - 1, p1 - +9 + j, p1 - 7 + j);
                }
            }

            if (top) {
                for (int j = (left ? 0 : -192); j < (right ? -64 : 64); j += 64) {
                    int q = p1 + j;
                    postFilter2x2Stage1(p, q + 5, q + 4, q + 64, q + 65);
                    postFilter2x2Stage1(p, q + 7, q + 6, q + 66, q + 67);
                    postFilter4x4Stage1(p, p1 + j, 0);
                }
            } else if (bottom) {
                for (int j = (left ? 0 : -192); j < (right ? -64 : 64); j += 64) {
                    postFilter4x4Stage1(p, p0 + 16 + j, 0);
                    postFilter4x4Stage1(p, p0 + 32 + j, 0);
                    int q = p0 + 48 + j;
                    postFilter2x2Stage1(p, q + 15, q + 14, q + 74, q + 75);
                    postFilter2x2Stage1(p, q + 13, q + 12, q + 72, q + 73);
                }
            } else {
                for (int j = (left ? 0 : -192); j < (right ? -64 : 64); j += 64) {
                    postFilter4x4Stage1(p, p0 + 16 + j, 0);
                    postFilter4x4Stage1(p, p0 + 32 + j, 0);
                    postFilter4x4Stage1Split(p, p0 + 48 + j, p1 + j, 0);
                    postFilter4x4Stage1(p, p1 + j, 0);
                }
            }
        }
    }


    private static void invOdd(int[] p, int ia, int ib, int ic, int id) {
        int a = p[ia];
        int b = p[ib];
        int c = p[ic];
        int d = p[id];

        b += d;
        a -= c;
        d -= b >> 1;
        c += (a + 1) >> 1;

        a -= (b * 3 + 4) >> 3;
        b += (a * 3 + 4) >> 3;
        c -= (d * 3 + 4) >> 3;
        d += (c * 3 + 4) >> 3;

        c -= (b + 1) >> 1;
        d = ((a + 1) >> 1) - d;
        b += c;
        a -= d;

        p[ia] = a;
        p[ib] = b;
        p[ic] = c;
        p[id] = d;
    }

    private static void invOddOdd(int[] p, int ia, int ib, int ic, int id) {
        int a = p[ia];
        int b = p[ib];
        int c = p[ic];
        int d = p[id];

        d += a;
        c -= b;
        int t1 = d >> 1;
        int t2 = c >> 1;
        a -= t1;
        b += t2;

        a -= (b * 3 + 3) >> 3;
        b += (a * 3 + 3) >> 2;
        a -= (b * 3 + 4) >> 3;

        b -= t2;
        a += t1;
        c += b;
        d -= a;

        p[ia] = a;
        p[ib] = -b;
        p[ic] = -c;
        p[id] = d;
    }

    private static void invOddOddPost(int[] p, int ia, int ib, int ic, int id) {
        int a = p[ia];
        int b = p[ib];
        int c = p[ic];
        int d = p[id];

        d += a;
        c -= b;
        int t1 = d >> 1;
        int t2 = c >> 1;
        a -= t1;
        b += t2;

        a -= (b * 3 + 6) >> 3;
        b += (a * 3 + 2) >> 2;
        a -= (b * 3 + 4) >> 3;

        b -= t2;
        a += t1;
        c += b;
        d -= a;

        p[ia] = a;
        p[ib] = b;
        p[ic] = c;
        p[id] = d;
    }

    private static void iRotate1(int[] p, int ia, int ib) {
        int a = p[ia];
        int b = p[ib];

        a -= (b + 1) >> 1;
        b += (a + 1) >> 1;

        p[ia] = a;
        p[ib] = b;
    }

    private static void postFilter2x2Stage1(int[] p, int off) {
        postFilter2x2Stage1(p, off, off + 1, off + 2, off + 3);
    }

    private static void hadamard2x2(int[] p, int ia, int ib, int ic, int id, int r) {
        int a = p[ia];
        int b = p[ib];
        int C = p[ic];
        int d = p[id];

        a += d;
        b -= C;
        int t = (a - b + r) >> 1;
        int c = t - d;
        d = t - C;
        a -= d;
        b += c;

        p[ia] = a;
        p[ib] = b;
        p[ic] = c;
        p[id] = d;
    }

    private static void strHSTdec(int[] p, int ia, int ib, int ic, int id) {
        int a = p[ia];
        int b = p[ib];
        int c = p[ic];
        int d = p[id];

        b -= c;
        // DEVIATION: hdpref rotates as `(d * 3 + 4) >> 3`
        a += (d * 3 + 8) >> 4;

        d -= (b >> 1);
        c = ((a - b) >> 1) - c;

        p[ic] = d;
        p[id] = c;
        p[ia] = a - c;
        p[ib] = b + d;
    }

    private static void strHSTdec1(int[] p, int ia, int id) {
        int a = p[ia];
        int d = p[id];

        a += d;
        d = (a >> 1) - d;
        // DEVIATION: hdpref rotates as `(d * 3) >> 3` and `(a * 3) >> 4`
        a += (d * 3 + 8) >> 4;
        d += (a * 3 + 4) >> 5;

        p[ia] = a;
        p[id] = d;
    }

    private static void invTransform4x4Stage1(int[] p, int off) {
        hadamard2x2(p, off, off + 1, off + 2, off + 3, 1);

        invOdd(p, off + +5, off + +4, off + +7, off + +6);
        invOdd(p, off + 10, off + +8, off + 11, off + +9);

        invOddOdd(p, off + 15, off + 14, off + 13, off + 12);

        hadamard2x2(p, off/**/, off + 4, off + +8, off + 12, 0);
        hadamard2x2(p, off + 1, off + 5, off + +9, off + 13, 0);
        hadamard2x2(p, off + 2, off + 6, off + 10, off + 14, 0);
        hadamard2x2(p, off + 3, off + 7, off + 11, off + 15, 0);
    }

    private static void invTransform4x4Stage2(int[] p, int off) {
        invOdd(p, off + +32, off + +48, off + +96, off + 112);
        invOdd(p, off + 128, off + 192, off + 144, off + 208);

        invOddOdd(p, off + 160, off + 224, off + 176, off + 240);

        hadamard2x2(p, off, off + 64, off + 16, off + 80, 1);

        hadamard2x2(p, off /**/, off + 192, off + +48, off + 240, 0);
        hadamard2x2(p, off + 64, off + 128, off + 112, off + 176, 0);
        hadamard2x2(p, off + 16, off + 208, off + +32, off + 224, 0);
        hadamard2x2(p, off + 80, off + 144, off + +96, off + 160, 0);
    }

    private static void postFilter2x2Stage1(int[] p, int ia, int ib, int ic, int id) {
        int a = p[ia];
        int b = p[ib];
        int c = p[ic];
        int d = p[id];

        a += d;
        b += c;
        d -= (a + 1) >> 1;
        c -= (b + 1) >> 1;

        c -= (d + 1) >> 1;
        d += (c + 1) >> 1;

        d += (a + 1) >> 1;
        c += (b + 1) >> 1;

        a -= d - (((d * 3) + 16) >> 5);
        b -= c - (((c * 3) + 16) >> 5);
        d += ((a * 3) + 8) >> 4;
        c += ((b * 3) + 8) >> 4;
        a += ((d * 3) + 16) >> 5;
        b += ((c * 3) + 16) >> 5;

        p[ia] = a;
        p[ib] = b;
        p[ic] = c;
        p[id] = d;
    }

    private static void postFilter4x4Stage1(int[] p, int off, int iOffset) {
        postFilter4x4Stage1Split(p, off, off + 16, iOffset);
    }

    private static void postFilter4x4Stage1Split(int[] p, int p0, int p1, int iOffset) {
        int p2 = p0 + 72 - iOffset;
        int p3 = p1 + 64 - iOffset;
        p0 += 12;
        p1 += 4;

        hadamard2x2(p, p0/**/, p2/**/, p1/**/, p3/**/, 0);
        hadamard2x2(p, p0 + 1, p2 + 1, p1 + 1, p3 + 1, 0);
        hadamard2x2(p, p0 + 2, p2 + 2, p1 + 2, p3 + 2, 0);
        hadamard2x2(p, p0 + 3, p2 + 3, p1 + 3, p3 + 3, 0);

        invOddOddPost(p, p3/**/, p3 + 1, p3 + 2, p3 + 3);

        iRotate1(p, p1 + 2, p1 + 3);
        iRotate1(p, p1/**/, p1 + 1);
        iRotate1(p, p2 + 1, p2 + 3);
        iRotate1(p, p2/**/, p2 + 2);

        strHSTdec1(p, p0/**/, p3/**/);
        strHSTdec1(p, p0 + 1, p3 + 1);
        strHSTdec1(p, p0 + 2, p3 + 2);
        strHSTdec1(p, p0 + 3, p3 + 3);
        strHSTdec(p, p0/**/, p2/**/, p1/**/, p3/**/);
        strHSTdec(p, p0 + 1, p2 + 1, p1 + 1, p3 + 1);
        strHSTdec(p, p0 + 2, p2 + 2, p1 + 2, p3 + 2);
        strHSTdec(p, p0 + 3, p2 + 3, p1 + 3, p3 + 3);
    }
}
