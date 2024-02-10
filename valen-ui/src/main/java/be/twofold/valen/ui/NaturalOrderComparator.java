package be.twofold.valen.ui;

import java.util.*;

public final class NaturalOrderComparator implements Comparator<String> {
    private static final Comparator<String> INSTANCE = new NaturalOrderComparator();

    private NaturalOrderComparator() {
    }

    public static Comparator<String> instance() {
        return INSTANCE;
    }

    @Override
    public int compare(String o1, String o2) {
        int ia = 0;
        int ib = 0;
        while (true) {
            int nza = 0;
            int nzb = 0;

            char ca = charAt(o1, ia);
            char cb = charAt(o2, ib);

            while (Character.isSpaceChar(ca) || ca == '0') {
                if (ca == '0') {
                    nza++;
                } else {
                    nza = 0;
                }

                ca = charAt(o1, ++ia);
            }

            while (Character.isSpaceChar(cb) || cb == '0') {
                if (cb == '0') {
                    nzb++;
                } else {
                    nzb = 0;
                }

                cb = charAt(o2, ++ib);
            }

            // Process run of digits
            if (Character.isDigit(ca) && Character.isDigit(cb)) {
                int bias = compareRight(o1.substring(ia), o2.substring(ib));
                if (bias != 0) {
                    return bias;
                }
            }

            if (ca == 0 && cb == 0) {
                return compareEqual(o1, o2, nza, nzb);
            }
            if (ca < cb) {
                return -1;
            }
            if (ca > cb) {
                return +1;
            }

            ia++;
            ib++;
        }
    }

    int compareRight(String a, String b) {
        int bias = 0;
        for (int ia = 0, ib = 0; ; ia++, ib++) {
            char ca = charAt(a, ia);
            char cb = charAt(b, ib);

            if (!isDigit(ca) && !isDigit(cb)) {
                return bias;
            }
            if (!isDigit(ca)) {
                return -1;
            }
            if (!isDigit(cb)) {
                return +1;
            }
            if (ca == 0 && cb == 0) {
                return bias;
            }

            if (bias == 0) {
                if (ca < cb) {
                    bias = -1;
                } else if (ca > cb) {
                    bias = +1;
                }
            }
        }
    }

    static boolean isDigit(char c) {
        return Character.isDigit(c) || c == '.' || c == ',';
    }

    static char charAt(String s, int i) {
        return i >= s.length() ? 0 : s.charAt(i);
    }

    static int compareEqual(String a, String b, int nza, int nzb) {
        if (nza - nzb != 0) {
            return nza - nzb;
        }

        if (a.length() == b.length()) {
            return a.compareTo(b);
        }

        return a.length() - b.length();
    }
}
