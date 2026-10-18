package be.twofold.valen.core.game;

import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.util.*;

import java.nio.file.*;

public record AssetPath(String value) implements Comparable<AssetPath> {
    private static final AssetPath ROOT = new AssetPath("");

    public AssetPath {
        Check.nonNull(value, "value");
        Check.argument(!value.startsWith("/") && !value.endsWith("/"), () -> "Invalid asset path: " + value);
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

    public AssetPath parent() {
        return new AssetPath(Filenames.getPath(value));
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

    public Path toPath(Path root) {
        var result = root;
        for (String segment : value.split("/")) {
            if (!segment.isEmpty()) {
                result = result.resolve(segment);
            }
        }
        return result;
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
