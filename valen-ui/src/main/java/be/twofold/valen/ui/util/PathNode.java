package be.twofold.valen.ui.util;

import java.util.*;

public final class PathNode<T> {
    private final T name;
    private final boolean hasFiles;
    private final Map<T, PathNode<T>> children = new HashMap<>();

    public PathNode(T name, boolean hasFiles) {
        this.name = name;
        this.hasFiles = hasFiles;
    }

    public T name() {
        return name;
    }

    public boolean hasFiles() {
        return hasFiles;
    }

    public Map<T, PathNode<T>> children() {
        return children;
    }

    public PathNode<T> get(T name, boolean hasFiles) {
        return children.computeIfAbsent(name, s -> new PathNode<>(s, hasFiles));
    }
}
