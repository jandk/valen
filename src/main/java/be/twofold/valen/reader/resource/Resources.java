package be.twofold.valen.reader.resource;

import java.util.*;

public record Resources(
    ResourcesHeader header,
    List<ResourcesEntry> entries,
    List<String> pathStrings,
    List<ResourcesDependency> dependencyEntries,
    int[] dependencyIndexes,
    int[] pathStringIndexes
) {
    @Override
    public String toString() {
        return "Resources{" +
               "header=" + header + ", " +
               "entries=" + entries.size() + " entries, " +
               "pathStrings=" + pathStrings.size() + " strings, " +
               "dependencyEntries=" + dependencyEntries + " entries, " +
               "dependencyIndexes=" + dependencyIndexes.length + " indexes, " +
               "pathStringIndexes=" + pathStringIndexes.length + " indexes" +
               "}";
    }
}
