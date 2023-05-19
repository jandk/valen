package be.twofold.valen.reader.resource;

import java.util.*;

public record Resources(
    ResourcesHeader header,
    List<ResourcesEntry> entries,
    List<String> strings,
    List<ResourcesDependency> dependencyEntries,
    int[] dependencyIndexes
) {
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Resources[");
        builder.append("header=").append(header).append(", ");
        builder.append("entries=").append(entries.size()).append(" entries, ");
        builder.append("strings=").append(strings.size()).append(" strings");
        if (dependencyEntries != null) {
            builder.append(", ");
            builder.append("dependencyEntries=").append(dependencyEntries).append(" entries, ");
            builder.append("dependencyIndexes=").append(dependencyIndexes.length).append(" indexes");
        }
        builder.append("]");
        return builder.toString();
    }
}
