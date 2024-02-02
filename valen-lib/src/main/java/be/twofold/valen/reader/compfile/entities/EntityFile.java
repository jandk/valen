package be.twofold.valen.reader.compfile.entities;

import java.util.*;

public record EntityFile(
    int version,
    int hierarchyVersion,
    Map<String, Entity> entities
) {
    @Override
    public String toString() {
        return "EntityFile(" +
            "version=" + version + ", " +
            "hierarchyVersion=" + hierarchyVersion + ", " +
            "entities={" + entities.size() + " entities}" +
            ")";
    }
}
