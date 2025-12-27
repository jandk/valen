package be.twofold.valen.game.source.readers.keyvalue;

import wtf.reversed.toolbox.util.*;

final class Source {
    private final String source;
    private int index = 0;

    Source(String source) {
        this.source = Check.nonNull(source, "source");
    }

    int index() {
        return index;
    }

    String subString(int start, int end) {
        return source.substring(start, end);
    }


    char peek() {
        return source.charAt(index);
    }

    char peekNext() {
        return source.charAt(index + 1);
    }

    char next() {
        return source.charAt(index++);
    }

    void skip() {
        skip(1);
    }

    void skip(int count) {
        index += count;
    }

    boolean isEof() {
        return index >= source.length();
    }
}
