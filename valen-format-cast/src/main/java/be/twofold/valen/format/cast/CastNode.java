package be.twofold.valen.format.cast;

import java.util.*;
import java.util.stream.*;

public record CastNode(
    CastNodeID identifier,
    long nodeHash,
    List<CastProperty> properties,
    List<CastNode> children
) {
    public CastNode {
        Objects.requireNonNull(identifier);
        Objects.requireNonNull(properties);
        Objects.requireNonNull(children);
    }

    public CastNode(CastNodeID identifier, long nodeHash) {
        this(identifier, nodeHash, new ArrayList<>(), new ArrayList<>());
    }

    public int length() {
        int result = 24;
        for (CastProperty property : properties) {
            result += property.length();
        }
        for (CastNode child : children) {
            result += child.length();
        }
        return result;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public CastNode addProperty(Optional<CastProperty> property) {
        property.ifPresent(properties::add);
        return this;
    }

    public CastNode addProperty(CastProperty property) {
        properties.add(property);
        return this;
    }

    public CastNode addChild(CastNode child) {
        children.add(child);
        return this;
    }

    public CastNode addChildren(Stream<CastNode> nodes) {
        nodes.forEach(children::add);
        return this;
    }

    @Override
    public String toString() {
        return "CastNode(" +
            "identifier=" + identifier + ", " +
            "nodeHash=" + nodeHash + ", " +
            "properties=[" + properties.size() + " elements], " +
            "children=[" + children.size() + " elements]" +
            ")";
    }
}
