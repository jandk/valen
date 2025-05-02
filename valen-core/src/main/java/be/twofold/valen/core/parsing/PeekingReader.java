package be.twofold.valen.core.parsing;

import be.twofold.valen.core.util.*;

import java.io.*;

public final class PeekingReader {
    private static final int NotPeeked = -2;

    private final Reader reader;
    private int peeked = NotPeeked;
    private int line = 1;
    private int column = 1;

    public PeekingReader(Reader reader) {
        this.reader = Check.notNull(reader, "reader");
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public int peek() {
        if (peeked == NotPeeked) {
            peeked = readChar();
        }
        return peeked;
    }

    public int read() {
        if (peeked == NotPeeked) {
            return readChar();
        }
        int result = peeked;
        peeked = NotPeeked;
        if (result == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return result;
    }

    public boolean isEof() {
        return peek() == -1;
    }

    private int readChar() {
        try {
            int high = reader.read();
            if (high < 0 || !Character.isSurrogate((char) high)) {
                return high;
            }
            if (Character.isLowSurrogate((char) high)) {
                throw new IOException("Unpaired low surrogate");
            }

            int low = reader.read();
            if (low < 0 || Character.isHighSurrogate((char) low)) {
                throw new IOException("Unpaired high surrogate");
            }

            return Character.toCodePoint((char) high, (char) low);
        } catch (IOException e) {
            throw new UncheckedIOException("Unexpected I/O error", e);
        }
    }
}
