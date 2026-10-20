package be.twofold.valen.game.idtech.game;

import wtf.reversed.toolbox.util.*;

import java.util.*;
import java.util.concurrent.*;

public final class ResourceType implements Comparable<ResourceType> {
    private static final Map<String, ResourceType> CACHE = new ConcurrentHashMap<>();

    public static final ResourceType ANIM = of("anim");
    public static final ResourceType IMAGE = of("image");
    public static final ResourceType MODEL = of("model");

    private final String name;

    private ResourceType(String name) {
        this.name = Check.nonNull(name, "name");
    }

    public static ResourceType of(String type) {
        return CACHE.computeIfAbsent(type, ResourceType::new);
    }

    @Override
    public int compareTo(ResourceType o) {
        return name.compareToIgnoreCase(o.name);
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
