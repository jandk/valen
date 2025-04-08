package be.twofold.valen.format.png;

final class PngFilter {
    private final PngFormat format;
    private final byte[][] filtered;
    // private final int[] filterCounts = new int[5];
    private byte[] previous;
    private byte[] current;

    public PngFilter(PngFormat format) {
        this.filtered = new byte[5][format.bytesPerPixel() + format.bytesPerRow()];
        this.previous = new byte[format.bytesPerPixel() + format.bytesPerRow()];
        this.current = new byte[format.bytesPerPixel() + format.bytesPerRow()];
        this.format = format;
    }

    public int filter(byte[] row, int offset) {
        int bpp = format.bytesPerPixel();
        int bpr = format.bytesPerRow();

        System.arraycopy(row, offset, current, bpp, bpr);
        int best = filter(null, current, previous, filtered, bpp, bpr);

        byte[] temp = previous;
        previous = current;
        current = temp;
        return best;
    }

    public byte[] filtered(int filter) {
        return filtered[filter];
    }

    private static int filter(PngColorType colorType, byte[] curr, byte[] prev, byte[][] scratch, int bpp, int bpr) {
        // if (colorType == PngColorType.Palette) {
        //     System.arraycopy(curr, bytesPerPixel, scratch[0], bytesPerPixel, bytesPerRow);
        //     return 0;
        // }

        int[] sad = new int[5];
        sad[0] = sad(curr, bpp, bpr);
        filterSub(curr, prev, scratch[1], bpp, bpr);
        sad[1] = sad(scratch[1], bpp, bpr);
        filterUp(curr, prev, scratch[2], bpp, bpr);
        sad[2] = sad(scratch[2], bpp, bpr);
        filterAverage(curr, prev, scratch[3], bpp, bpr);
        sad[3] = sad(scratch[3], bpp, bpr);
        filterPaeth(curr, prev, scratch[4], bpp, bpr);
        sad[4] = sad(scratch[4], bpp, bpr);

        int bestRow = 0;
        int bestSad = sad[0];
        for (int i = 1; i < 5; i++) {
            if (sad[i] < bestSad) {
                bestRow = i;
                bestSad = sad[i];
            }
        }

        if (bestRow == 0) {
            System.arraycopy(curr, bpp, scratch[0], bpp, bpr);
        }

        return bestRow;
    }

    private static void filterSub(byte[] curr, byte[] prev, byte[] out, int bpp, int bpr) {
        for (int i = bpp; i < bpp + bpr; i++) {
            int x = Byte.toUnsignedInt(curr[i]);
            int a = Byte.toUnsignedInt(curr[i - bpp]);

            out[i] = (byte) (x - a);
        }
    }

    private static void filterUp(byte[] curr, byte[] prev, byte[] out, int bpp, int bpr) {
        for (int i = bpp; i < bpp + bpr; i++) {
            int x = Byte.toUnsignedInt(curr[i]);
            int b = Byte.toUnsignedInt(prev[i]);

            out[i] = (byte) (x - b);
        }
    }

    private static void filterAverage(byte[] curr, byte[] prev, byte[] out, int bpp, int bpr) {
        for (int i = bpp; i < bpp + bpr; i++) {
            int x = Byte.toUnsignedInt(curr[i]);
            int a = Byte.toUnsignedInt(curr[i - bpp]);
            int b = Byte.toUnsignedInt(prev[i]);

            out[i] = (byte) (x - (a + b >> 1));
        }
    }

    private static void filterPaeth(byte[] curr, byte[] prev, byte[] out, int bpp, int bpr) {
        for (int i = bpp; i < bpp + bpr; i++) {
            int x = Byte.toUnsignedInt(curr[i]);
            int a = Byte.toUnsignedInt(curr[i - bpp]);
            int b = Byte.toUnsignedInt(prev[i]);
            int c = Byte.toUnsignedInt(prev[i - bpp]);

            out[i] = (byte) (x - paeth(a, b, c));
        }
    }

    private static int sad(byte[] array, int offset, int length) {
        int lim = offset + length;
        int sad0 = 0, sad1 = 0, sad2 = 0, sad3 = 0;
        int i;
        for (i = offset; i < lim - 4; i += 4) {
            sad0 += Math.abs(array[i/**/]);
            sad1 += Math.abs(array[i + 1]);
            sad2 += Math.abs(array[i + 2]);
            sad3 += Math.abs(array[i + 3]);
        }

        int sad = sad0 + sad1 + sad2 + sad3;
        for (; i < lim; i++) {
            sad += Math.abs(array[i]);
        }
        return sad;
    }

    private static int paeth(int a, int b, int c) {
        var thresh = c * 3 - (a + b);
        var lo = Math.min(a, b);
        var hi = Math.max(a, b);
        var t0 = hi <= thresh ? lo : c;
        return thresh <= lo ? hi : t0;
    }
}
