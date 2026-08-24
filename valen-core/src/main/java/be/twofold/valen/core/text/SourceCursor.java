package be.twofold.valen.core.text;

import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;
import java.util.function.*;

public final class SourceCursor {
    public static final char EOF = '\0';

    private final String source;
    private int start;
    private int current;
    private int[] lineStarts;

    public SourceCursor(String source) {
        this.source = Check.nonNull(source, "source");
    }

    // Cursor

    public boolean isAtEnd() {
        return current >= source.length();
    }

    public char peek() {
        if (current >= source.length()) {
            return EOF;
        }
        return source.charAt(current);
    }

    public char peek(int offset) {
        if (current + offset >= source.length()) {
            return EOF;
        }
        return source.charAt(current + offset);
    }

    public char advance() {
        if (isAtEnd()) {
            return EOF;
        }
        return source.charAt(current++);
    }

    public int mark() {
        return current;
    }

    public void reset(int mark) {
        current = mark;
    }

    public void skip(int count) {
        current = Math.min(current + count, source.length());
    }

    // Matching

    public boolean match(char c) {
        if (isAtEnd() || source.charAt(current) != c) {
            return false;
        }

        current++;
        return true;
    }

    public boolean check(String s) {
        return source.startsWith(s, current);
    }

    public boolean match(String s) {
        if (check(s)) {
            current += s.length();
            return true;
        }
        return false;
    }

    public boolean matchIgnoreCase(char c) {
        if (isAtEnd() || Ascii.toLower(source.charAt(current)) != Ascii.toLower(c)) {
            return false;
        }

        current++;
        return true;
    }

    // Tokens

    public void startLexeme() {
        start = current;
    }

    public String lexeme() {
        return source.substring(start, current);
    }

    public int lexemeLine() {
        return lineIndexAt(start) + 1;
    }

    public int lexemeColumn() {
        return columnAt(start);
    }

    // Scanning

    public void skipWhile(IntPredicate predicate) {
        while (!isAtEnd() && predicate.test(peek())) {
            current++;
        }
    }

    public boolean skipPast(char c) {
        int index = source.indexOf(c, current);
        if (index == -1) {
            return false;
        }
        current = index + 1;
        return true;
    }

    // Positions

    public int line() {
        return lineIndexAt(current) + 1;
    }

    public int column() {
        return columnAt(current);
    }

    private int lineIndexAt(int index) {
        int line = Arrays.binarySearch(lineStarts(), index);
        return line >= 0 ? line : -line - 2;
    }

    private int columnAt(int index) {
        return index - lineStarts()[lineIndexAt(index)] + 1;
    }

    private int[] lineStarts() {
        if (lineStarts == null) {
            this.lineStarts = buildLineStarts();
        }
        return lineStarts;
    }

    private int[] buildLineStarts() {
        var lineStarts = new int[16];

        var count = 0;
        lineStarts[count++] = 0;

        for (int i = 0; i < source.length(); i++) {
            if (source.charAt(i) == '\n') {
                if (lineStarts.length == count) {
                    lineStarts = Arrays.copyOf(lineStarts, lineStarts.length * 2);
                }
                lineStarts[count++] = i + 1;
            }
        }

        return Arrays.copyOf(lineStarts, count);
    }

}
