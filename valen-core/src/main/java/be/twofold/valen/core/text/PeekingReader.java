package be.twofold.valen.core.text;

import wtf.reversed.toolbox.util.*;

import java.io.*;

/**
 * A {@link Reader} wrapper with <b>two</b> code points of lookahead, which is what a lexer needs to
 * recognise two-character openers like {@code /*} and {@code <%} without backing up.
 */
public final class PeekingReader {
    private static final int NOT_PEEKED = -2;
    private static final int EOF = -1;

    private final Reader reader;
    private int peeked = NOT_PEEKED;
    private int peekedNext = NOT_PEEKED;
    private int line = 1;
    private int column = 1;

    public PeekingReader(Reader reader) {
        this.reader = Check.nonNull(reader, "reader");
    }

    /**
     * The line the next {@link #read()} will return, 1 based.
     */
    public int line() {
        return line;
    }

    /**
     * The column the next {@link #read()} will return, 1 based. Counts code points, not chars.
     */
    public int column() {
        return column;
    }

    /**
     * The next code point, without consuming it, or {@code -1} at end of input.
     */
    public int peek() {
        if (peeked == NOT_PEEKED) {
            peeked = readCodePoint();
        }
        return peeked;
    }

    /**
     * The code point after {@link #peek()}, without consuming either, or {@code -1} at end of input.
     */
    public int peekNext() {
        peek();
        if (peekedNext == NOT_PEEKED) {
            peekedNext = readCodePoint();
        }
        return peekedNext;
    }

    /**
     * Consumes and returns the next code point, or {@code -1} at end of input.
     */
    public int read() {
        int result = peek();
        peeked = peekedNext;
        peekedNext = NOT_PEEKED;

        if (result == EOF) {
            return result;
        }

        if (result == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return result;
    }

    public boolean isEof() {
        return peek() == EOF;
    }

    private int readCodePoint() {
        try {
            int high = reader.read();
            if (high < 0 || !Character.isSurrogate((char) high)) {
                return high;
            }
            if (Character.isLowSurrogate((char) high)) {
                throw malformed("low surrogate with no high surrogate before it");
            }

            int low = reader.read();
            if (low < 0 || !Character.isLowSurrogate((char) low)) {
                throw malformed("high surrogate with no low surrogate after it");
            }

            return Character.toCodePoint((char) high, (char) low);
        } catch (IOException e) {
            throw new UncheckedIOException("Unexpected I/O error", e);
        }
    }

    private MalformedTextException malformed(String detail) {
        return new MalformedTextException("Malformed UTF-16 at " + line + ":" + column + ": " + detail);
    }
}
