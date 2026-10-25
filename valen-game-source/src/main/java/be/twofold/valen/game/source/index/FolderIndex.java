package be.twofold.valen.game.source.index;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.source.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record FolderIndex(
    List<SourceAsset> assets
) {
    public FolderIndex {
        assets = List.copyOf(assets);
    }

    public static FolderIndex build(Path root) throws IOException {
        try (var stream = Files.walk(root, 100)) {
            var assets = stream
                .filter(path -> Files.isRegularFile(path) && !path.getFileName().toString().endsWith(".vpk"))
                .map(path -> mapToAsset(root, path))
                .toList();

            return new FolderIndex(assets);
        }
    }

    private static SourceAsset mapToAsset(Path root, Path path) {
        var relativePath = root.relativize(path).toString().replace('\\', '/');
        var assetID = new SourceAssetID(relativePath);
        var location = new Location.FullFile(path);
        return new SourceAsset(assetID, location);
    }
}
