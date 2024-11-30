package be.twofold.valen.ui.util;

import java.util.*;

public final class PathNode<T> {
    private final T name;
    private final Map<T, PathNode<T>> children = new HashMap<>();

    public PathNode(T name) {
        this.name = name;
    }

    public T name() {
        return name;
    }

    public Map<T, PathNode<T>> children() {
        return children;
    }

    public PathNode<T> get(T name) {
        return children.computeIfAbsent(name, PathNode::new);
    }
}
