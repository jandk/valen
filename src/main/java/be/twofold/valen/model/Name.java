package be.twofold.valen.model;

import java.util.*;
import java.util.stream.*;

public record Name(
    String name,
    String path,
    String file,
    Map<String, String> properties
) {
    public static Name parse(String name) {
        int dollarIndex = name.indexOf('$');
        String fullPath = name.substring(0, dollarIndex < 0 ? name.length() : dollarIndex);

        int slashIndex = fullPath.lastIndexOf('/');
        String path = fullPath.substring(0, slashIndex < 0 ? fullPath.length() : slashIndex);
        String file = fullPath.substring(slashIndex < 0 ? 0 : slashIndex + 1);

        Map<String, String> properties = Arrays.stream(name.split("\\$"))
            .skip(1)
            .map(s -> s.split("="))
            .collect(Collectors.toUnmodifiableMap(s -> s[0], s -> s.length > 1 ? s[1] : s[0]));

        return new Name(name, path, file, properties);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Name)) return false;
        return name.equals(((Name) obj).name);
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
