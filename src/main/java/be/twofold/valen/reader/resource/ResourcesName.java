package be.twofold.valen.reader.resource;

import java.util.*;
import java.util.stream.*;

public record ResourcesName(
    String name,
    String path,
    String file,
    Map<String, String> properties
) {
    public static ResourcesName parse(String name) {
        int dollarIndex = name.indexOf('$');
        String fullPath = name.substring(0, dollarIndex < 0 ? name.length() : dollarIndex);

        int slashIndex = fullPath.lastIndexOf('/');
        String path = fullPath.substring(0, slashIndex < 0 ? fullPath.length() : slashIndex);
        String file = fullPath.substring(slashIndex < 0 ? 0 : slashIndex + 1);

        Map<String, String> properties = Arrays.stream(name.split("\\$"))
            .skip(1)
            .map(s -> s.split("="))
            .collect(Collectors.toUnmodifiableMap(s -> s[0], s -> s.length > 1 ? s[1] : s[0]));

        return new ResourcesName(name, path, file, properties);
    }

    public String fileWithoutExtension() {
        int dot = file.lastIndexOf('.');
        return dot < 0 ? file : file.substring(0, dot);
    }

    public String fileExtension() {
        int dot = file.lastIndexOf('.');
        return dot < 0 ? "" : file.substring(dot + 1);
    }

    public String fullPath() {
        return path + "/" + file;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ResourcesName other)) return false;
        return name.equals(other.name);
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
