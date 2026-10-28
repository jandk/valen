package be.twofold.valen.game.idtech.game;

import wtf.reversed.toolbox.util.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Represents a resource type in a modern idtech game.
 */
public final class ResourceType implements Comparable<ResourceType> {
    private static final Map<String, ResourceType> CACHE = new ConcurrentHashMap<>();

    private final String name;

    private ResourceType(String name) {
        this.name = Check.nonNull(name, "name");
    }

    public static ResourceType of(String type) {
        return CACHE.computeIfAbsent(type, ResourceType::new);
    }

    @Override
    public int compareTo(ResourceType o) {
        return name.compareTo(o.name);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourceType other
            && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }
}
