package be.twofold.valen.format.png;

final class PngFilter {
    private final int bpp;
    private final int bpr;
    private final PngFilterMethod method;
    private final byte[][] filtered;
    private byte[] previous;
    private byte[] current;

    public PngFilter(PngFormat format, PngFilterMethod method) {
        this.bpp = format.bytesPerPixel();
        this.bpr = format.bytesPerRow();
        this.filtered = new byte[5][bpp + bpr];
        this.previous = new byte[bpp + bpr];
        this.current = new byte[bpp + bpr];
        this.method = method;
    }

    public int filter(byte[] row, int offset) {
        System.arraycopy(row, offset, current, bpp, bpr);

        int filter = switch (method) {
            case NONE -> {
                System.arraycopy(current, bpp, filtered[0], bpp, bpr);
                yield 0;
            }
            case UP -> {
                filterUp();
                yield 2;
            }
            case ADAPTIVE -> {
                filterSub();
                filterUp();
                filterAvg();
                filterPaeth();
                yield bestSad();
            }
        };

        byte[] temp = previous;
        previous = current;
        current = temp;
        return filter;
    }

    public byte[] filtered(int filter) {
        return filtered[filter];
    }

    private int bestSad() {
        int filter = 0;
        int minSad = Integer.MAX_VALUE;
        for (int i = 1; i < 5; i++) {
            int sad = 0;
            byte[] row = filtered[i];
            for (int j = bpp, lim = bpp + bpr; j < lim; j++) {
                sad += Math.abs(row[j]);
            }
            if (sad < minSad) {
                filter = i;
                minSad = sad;
            }
        }
        return filter;
    }

    private void filterSub() {
        byte[] curr = current;
        byte[] row = filtered[1];
        for (int i = bpp, lim = bpp + bpr; i < lim; i++) {
            int x = Byte.toUnsignedInt(curr[i]);
            int a = Byte.toUnsignedInt(curr[i - bpp]);
            row[i] = (byte) (x - a);
        }
    }

    private void filterUp() {
        byte[] curr = current;
        byte[] prev = previous;
        byte[] row = filtered[2];
        for (int i = bpp, lim = bpp + bpr; i < lim; i++) {
            int x = Byte.toUnsignedInt(curr[i]);
            int b = Byte.toUnsignedInt(prev[i]);
            row[i] = (byte) (x - b);
        }
    }

    private void filterAvg() {
        byte[] curr = current;
        byte[] prev = previous;
        byte[] row = filtered[3];
        for (int i = bpp, lim = bpp + bpr; i < lim; i++) {
            int x = Byte.toUnsignedInt(curr[i]);
            int a = Byte.toUnsignedInt(curr[i - bpp]);
            int b = Byte.toUnsignedInt(prev[i]);
            row[i] = (byte) (x - ((a + b) >>> 1));
        }
    }

    private void filterPaeth() {
        byte[] curr = current;
        byte[] prev = previous;
        byte[] row = filtered[4];
        for (int i = bpp, lim = bpp + bpr; i < lim; i++) {
            int x = Byte.toUnsignedInt(curr[i]);
            int a = Byte.toUnsignedInt(curr[i - bpp]);
            int b = Byte.toUnsignedInt(prev[i]);
            int c = Byte.toUnsignedInt(prev[i - bpp]);
            row[i] = (byte) (x - paeth(a, b, c));
        }
    }

    private int paeth(int a, int b, int c) {
        var thresh = c * 3 - (a + b);
        var lo = Math.min(a, b);
        var hi = Math.max(a, b);
        var t0 = hi <= thresh ? lo : c;
        return thresh <= lo ? hi : t0;
    }
}
