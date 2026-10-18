package be.twofold.valen.core.game;

import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.util.*;

import java.nio.file.*;
import java.util.*;

public record AssetPath(String value) implements Comparable<AssetPath> {
    public static final AssetPath ROOT = new AssetPath("");

    public AssetPath {
        Check.nonNull(value, "value");
        Check.argument(
            !value.startsWith("/") && !value.endsWith("/") && !value.contains("//"),
            () -> "Invalid asset path: " + value);
    }

    public static AssetPath of(String value) {
        return new AssetPath(value);
    }

    public String fileName() {
        return Filenames.getName(value);
    }

    public String baseName() {
        return Filenames.getBaseName(value);
    }

    public String extension() {
        return Filenames.getExtension(value);
    }

    public boolean startsWith(AssetPath prefix) {
        return prefix.value.isEmpty()
            || value.equals(prefix.value)
            || value.startsWith(prefix.value + "/");
    }

    public AssetPath parent() {
        return new AssetPath(Filenames.getPath(value));
    }

    public AssetPath relativize(AssetPath prefix) {
        Check.argument(startsWith(prefix), () -> value + " does not start with " + prefix);
        if (prefix.value.isEmpty()) {
            return this;
        }
        return value.length() == prefix.value.length()
            ? ROOT
            : new AssetPath(value.substring(prefix.value.length() + 1));
    }

    public AssetPath resolve(String child) {
        return new AssetPath(value.isEmpty() ? child : value + "/" + child);
    }

    public AssetPath withExtension(String extension) {
        return new AssetPath(Filenames.replaceExtension(value, extension));
    }

    public AssetPath withSuffix(String suffix) {
        var extension = extension();
        var base = Filenames.removeExtension(value) + suffix;
        return new AssetPath(extension.isEmpty() ? base : base + "." + extension);
    }

    public AssetPath withoutExtension() {
        return new AssetPath(Filenames.removeExtension(value));
    }

    public List<String> segments() {
        return value.isEmpty()
            ? List.of()
            : List.of(value.split("/"));
    }

    public Path toPath(Path root) {
        return segments().stream()
            .filter(s -> !s.isEmpty())
            .reduce(root, Path::resolve, Path::resolve);
    }

    @Override
    public int compareTo(AssetPath o) {
        return value.compareTo(o.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
