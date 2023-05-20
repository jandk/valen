package be.twofold.valen.reader.resource;

import java.util.*;

public record Resources(
    ResourcesHeader header,
    List<ResourcesEntry> entries,
    List<ResourcesDependency> dependencies,
    int[] dependencyIndex
) {
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Resources[");
        builder.append("header=").append(header).append(", ");
        builder.append("entries=").append(entries.size()).append(" entries, ");
        if (dependencies != null) {
            builder.append(", ");
            builder.append("dependencies=").append(dependencies.size()).append(" entries, ");
            builder.append("dependencyIndex=").append(dependencyIndex.length).append(" indices, ");
        }
        builder.append("]");
        return builder.toString();
    }
}
