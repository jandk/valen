package be.twofold.valen.resource;

import java.util.*;
import java.util.stream.*;

public record ResourceName(
    String name
) implements Comparable<ResourceName> {
    public String fullPath() {
        var index = name.indexOf('$');
        return index < 0 ? name : name.substring(0, index);
    }

    public String path() {
        var fullPath = fullPath();
        var index = fullPath.lastIndexOf('/');
        return index < 0 ? "" : fullPath.substring(0, index);
    }

    public String file() {
        var fullPath = fullPath();
        var index = fullPath.lastIndexOf('/');
        return index < 0 ? fullPath : fullPath.substring(index + 1);
    }

    public String fileWithoutExtension() {
        var file = file();
        var index = file.lastIndexOf('.');
        return index < 0 ? file : file.substring(0, index);
    }

    public String extension() {
        var file = file();
        var index = file.lastIndexOf('.');
        return index < 0 ? "" : file.substring(index + 1);
    }

    public Map<String, String> attributes() {
        var index = name.indexOf('$');
        if (index < 0) {
            return Map.of();
        }

        var split = name.substring(index + 1).split("\\$");
        return Arrays.stream(split)
            .map(this::property)
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                Map.Entry::getValue
            ));
    }

    private Map.Entry<String, String> property(String s) {
        var index = s.indexOf('=');
        var key = index < 0 ? s : s.substring(0, index);
        var value = index < 0 ? s : s.substring(index + 1);
        return Map.entry(key, value);
    }

    @Override
    public int compareTo(ResourceName o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }
}
