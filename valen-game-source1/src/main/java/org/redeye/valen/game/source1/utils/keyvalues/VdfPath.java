package org.redeye.valen.game.source1.utils.keyvalues;

import java.util.*;

public record VdfPath(List<VdfPathElement> elements) {

    public static VdfPath of(String path) {
        var elements = new ArrayList<VdfPathElement>();
        for (String part : path.split("\\.")) {
            if (part.startsWith("[") && part.endsWith("]")) {
                elements.add(new VdfPathElement.Index(Integer.parseInt(part.substring(1, part.length() - 1))));
            } else {
                elements.add(new VdfPathElement.Key(part));
            }
        }
        return new VdfPath(elements);
    }

    public Optional<VdfValue> lookup(VdfValue object) {
        var current = object;
        for (VdfPathElement element : elements) {
            if (current == null) {
                return Optional.empty();
            }
            current = element.get(current);
        }
        return Optional.of(current);
    }
}
