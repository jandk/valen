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
        return "Resources[" +
               "header=" + header + ", " +
               "entries=" + entries.size() + " entries, " +
               "dependencies=" + dependencies.size() + " entries, " +
               "dependencyIndex=" + dependencyIndex.length + " indices" +
               "]";
    }
}
