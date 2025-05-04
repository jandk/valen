package be.twofold.valen.format.cast;

import java.util.*;

public final class Cast extends AbstractList<CastNode> {
    private final List<CastNode> rootNodes;

    public Cast() {
        this(new ArrayList<>());
    }

    Cast(List<CastNode> rootNodes) {
        this.rootNodes = Objects.requireNonNull(rootNodes);
    }

    @Override
    public int size() {
        return rootNodes.size();
    }

    @Override
    public CastNode get(int index) {
        return rootNodes.get(index);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Cast cast
            && rootNodes.equals(cast.rootNodes);
    }

    @Override
    public int hashCode() {
        return rootNodes.hashCode();
    }

    @Override
    public String toString() {
        return "Cast(" + rootNodes + ")";
    }
}
