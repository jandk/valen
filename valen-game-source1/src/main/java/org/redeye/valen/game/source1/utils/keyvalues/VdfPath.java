package org.redeye.valen.game.source1.utils.keyvalues;

import java.util.*;

public record VdfPath(List<VdfPathElement> elements) {
    public VdfPath {
        elements = List.copyOf(elements);
    }

    public static VdfPath of(String path) {
        var elements = new ArrayList<VdfPathElement>();
        for (var part : path.split("\\.")) {
            if (part.startsWith("[") && part.endsWith("]")) {
                int index = Integer.parseInt(part.substring(1, part.length() - 1));
                elements.add(new VdfPathElement.Index(index));
            } else {
                elements.add(new VdfPathElement.Key(part));
            }
        }
        return new VdfPath(elements);
    }

    public Optional<VdfValue> lookup(VdfValue object) {
        var current = object;
        for (var element : elements) {
            if (current == null) {
                return Optional.empty();
            }
            current = element.get(current);
        }
        return Optional.of(current);
    }
}
