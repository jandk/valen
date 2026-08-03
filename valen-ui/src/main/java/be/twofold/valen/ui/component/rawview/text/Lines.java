package be.twofold.valen.ui.component.rawview.text;

import wtf.reversed.toolbox.util.*;

import java.util.*;

final class Lines {
    private final String text;
    private final int[] indices;

    private Lines(String text, int[] indices) {
        this.text = Check.nonNull(text, "text");
        this.indices = Check.nonNull(indices, "indices");
    }

    public static Lines parse(String text) {
        var indices = new int[16];
        var count = 0;
        indices[count++] = 0;

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                if (indices.length == count) {
                    indices = Arrays.copyOf(indices, indices.length * 2);
                }
                indices[count++] = i + 1;
            }
        }

        indices = Arrays.copyOf(indices, count + 1);
        indices[count] = text.length() + 1;
        return new Lines(text, indices);
    }

    public String text() {
        return text;
    }

    public int size() {
        return indices.length - 1;
    }

    public String get(int index) {
        int start = start(index);
        int end = indices[index + 1] - 1;
        if (end > start && text.charAt(end - 1) == '\r') {
            end--;
        }
        return text.substring(start, end);
    }

    public int start(int row) {
        Check.index(row, size());
        return indices[row];
    }

    public int row(int offset) {
        Check.position(offset, text.length(), "offset");
        int index = Arrays.binarySearch(indices, offset);
        return index < 0 ? -index - 2 : index;
    }
}
