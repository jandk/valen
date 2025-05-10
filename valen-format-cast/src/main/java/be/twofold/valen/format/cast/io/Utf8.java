package be.twofold.valen.format.cast.io;

public final class Utf8 {
    private Utf8() {
    }

    public static int length(CharSequence s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int cp = s.charAt(i);
            if (Character.isSurrogate((char) cp)) {
                if (Character.isLowSurrogate((char) cp)) {
                    throw new IllegalArgumentException("Unpaired low surrogate");
                }

                int low = s.charAt(++i);
                if (Character.isHighSurrogate((char) low)) {
                    throw new IllegalArgumentException("Unpaired high surrogate");
                }

                cp = Character.toCodePoint((char) cp, (char) low);
            }

            length += length(cp);
        }
        return length;
    }

    private static int length(int codePoint) {
        if (codePoint < 0x80) {
            return 1;
        } else if (codePoint < 0x800) {
            return 2;
        } else if (codePoint < 0x10000) {
            return 3;
        } else {
            return 4;
        }
    }
}
