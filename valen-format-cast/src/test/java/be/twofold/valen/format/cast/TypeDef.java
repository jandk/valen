package be.twofold.valen.format.cast;

import be.twofold.valen.format.cast.node.*;

import java.util.*;

record TypeDef(
    CastNodeID type,
    List<CastNodeID> children,
    List<PropertyDef> properties
) {
    public TypeDef {
        Objects.requireNonNull(type);
        children = List.copyOf(children);
        properties = List.copyOf(properties);
    }

    @Override
    public String toString() {
        var builder = new StringBuilder("TypeDef(")
            .append("type=").append(type).append(", ")
            .append("children=").append(children).append(", ")
            .append("properties=[");

        for (PropertyDef property : properties) {
            builder.append("\n\t").append(property);
        }

        return builder.append("\n])").toString();
    }
}
