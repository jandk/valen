package org.redeye.valen.game.source1.keyvalue;

import be.twofold.valen.core.util.*;

public final class Source {
    private final String source;
    private int index = 0;

    public Source(String source) {
        this.source = Check.notNull(source, "source");
    }

    public int index() {
        return index;
    }

    public String subString(int start, int end) {
        return source.substring(start, end);
    }


    public char peek() {
        return source.charAt(index);
    }

    public char peekNext() {
        return source.charAt(index + 1);
    }

    public char next() {
        return source.charAt(index++);
    }

    public void skip() {
        skip(1);
    }

    public void skip(int count) {
        index += count;
    }

    public boolean isEof() {
        return index >= source.length();
    }
}
