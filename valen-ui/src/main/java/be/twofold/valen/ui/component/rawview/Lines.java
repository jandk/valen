package be.twofold.valen.ui.component.rawview;

import be.twofold.valen.core.util.*;

import java.util.*;

final class Lines extends AbstractList<String> {
    private final String source;
    private final int[] indices;

    private Lines(String source, List<Integer> indices) {
        this.source = Check.nonNull(source, "source");
        this.indices = indices.stream()
            .mapToInt(Integer::intValue)
            .toArray();
    }

    public static Lines parse(String source) {
        var indices = new ArrayList<Integer>();
        indices.add(0);
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if (c == '\n') {
                indices.add(i + 1);
            }
        }
        indices.add(source.length());
        return new Lines(source, indices);
    }

    @Override
    public String get(int index) {
        Check.index(index, size());
        return source.substring(indices[index], indices[index + 1]);
    }

    @Override
    public int size() {
        return indices.length - 1;
    }
}
