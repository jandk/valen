package be.twofold.valen.ui.component.filelist;

import java.util.*;

final class PathNode<T> {
    private final T name;
    private final boolean hasFiles;
    private final Map<T, PathNode<T>> children = new HashMap<>();

    PathNode(T name, boolean hasFiles) {
        this.name = name;
        this.hasFiles = hasFiles;
    }

    T name() {
        return name;
    }

    boolean hasFiles() {
        return hasFiles;
    }

    Map<T, PathNode<T>> children() {
        return children;
    }

    PathNode<T> get(T name, boolean hasFiles) {
        return children.computeIfAbsent(name, s -> new PathNode<>(s, hasFiles));
    }
}
