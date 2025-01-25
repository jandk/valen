package be.twofold.valen.core.util;

import java.util.*;

/**
 * Plainly yeeted from <a href="https://github.com/ShadelessFox/decima">Decima Workshop</a>
 * <p>
 * It's ok, we steal each other's code all the time ;-)
 */
public final class AlphanumericComparator implements Comparator<CharSequence> {
    private static final AlphanumericComparator INSTANCE = new AlphanumericComparator();

    private AlphanumericComparator() {
    }

    public static AlphanumericComparator instance() {
        return INSTANCE;
    }

    @Override
    public int compare(CharSequence o1, CharSequence o2) {
        int len1 = o1.length();
        int len2 = o2.length();

        for (int i1 = 0, i2 = 0; i1 < len1 && i2 < len2; ) {
            char c1 = o1.charAt(i1);
            char c2 = o2.charAt(i2);

            if (Character.isDigit(c1) && Character.isDigit(c2)) {
                long n1 = 0;
                while (i1 < len1 && Character.isDigit(o1.charAt(i1))) {
                    n1 = n1 * 10 + Character.digit(o1.charAt(i1), 10);
                    i1++;
                }

                long n2 = 0;
                while (i2 < len2 && Character.isDigit(o2.charAt(i2))) {
                    n2 = n2 * 10 + Character.digit(o2.charAt(i2), 10);
                    i2++;
                }

                if (n1 != n2) {
                    return Long.compare(n1, n2);
                }
            } else {
                if (c1 != c2) {
                    return Character.compare(c1, c2);
                }

                i1++;
                i2++;
            }
        }

        return Integer.compare(len1, len2);
    }
}
