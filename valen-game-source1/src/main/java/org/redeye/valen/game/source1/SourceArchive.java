package org.redeye.valen.game.source1;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import org.redeye.valen.game.source1.providers.*;
import org.redeye.valen.game.source1.readers.*;
import org.redeye.valen.game.source1.readers.gameinfo.*;
import org.redeye.valen.game.source1.readers.vtf.*;
import org.redeye.valen.game.source1.vpk.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class SourceArchive implements Archive {
    private static final AssetReaders<SourceAsset> READERS = new AssetReaders<>(List.of(
        new KeyValueReader(),
        new VmtReader(),
        new VtfReader()
    ));

    private final String name;
    private final Container<SourceAssetID, SourceAsset> container;

    public SourceArchive(Path modRoot) throws IOException {
        this.name = modRoot.getFileName().toString();
        var gameRoot = modRoot.getParent();

        var wanted = Set.of("game", "mod", "platform");
        var gameInfo = GameInfo.read(modRoot.resolve("gameinfo.txt"));
        var searchPaths = gameInfo.fileSystem().searchPaths().stream()
            .filter(e -> wanted.contains(e.getKey()))
            .flatMap(e -> parseSearchPath(e.getValue(), gameRoot, modRoot).stream())
            .distinct()
            .toList();

        List<Container<SourceAssetID, SourceAsset>> containers = new ArrayList<>();
        for (var searchPath : searchPaths) {
            if (searchPath.getFileName().toString().endsWith(".vpk")) {
                if (Files.notExists(searchPath)) {
                    var name = searchPath.getFileName().toString();
                    name = name.substring(0, name.length() - 4);
                    searchPath = searchPath.getParent().resolve(name + "_dir.vpk");
                }
                if (Files.exists(searchPath)) {
                    containers.add(new VpkCollection(searchPath));
                }
            } else {
                containers.add(new FolderProvider(searchPath));
            }
        }
        container = Container.compose(containers);
    }

    public static List<Path> parseSearchPath(String pathString, Path gameRoot, Path modRoot) {
        // Check for a wildcard first
        boolean wildcard = false;
        if (pathString.endsWith("*")) {
            pathString = pathString.substring(0, pathString.length() - 1);
            wildcard = true;
        }

        // Or a full stop
        boolean fullStop = false;
        if (pathString.endsWith(".")) {
            pathString = pathString.substring(0, pathString.length() - 1);
            fullStop = true;
        }

        // Replace placeholders with actual values
        Path path;
        if (pathString.startsWith("|gameinfo_path|")) {
            path = fullStop ? modRoot : modRoot.resolve(pathString.substring("|gameinfo_path|".length()));
        } else if (pathString.contains("|all_source_engine_paths|")) {
            path = fullStop ? gameRoot : gameRoot.resolve(pathString.substring("|all_source_engine_paths|".length()));
        } else {
            path = gameRoot.resolve(pathString);
        }

        // Scan in case of wildcard
        if (!wildcard) {
            return List.of(path);
        }
        if (!Files.isDirectory(path)) {
            return List.of();
        }
        try (var stream = Files.list(path)) {
            return stream.toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public Stream<? extends Asset> assets() {
        return container.getAll();
    }

    @Override
    public Optional<? extends Asset> getAsset(AssetID identifier) {
        return container.get((SourceAssetID) identifier);
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        var asset = container.get((SourceAssetID) identifier)
            .orElseThrow(FileNotFoundException::new);

        var buffer = container.read(asset.id());

        try (var source = DataSource.fromBuffer(buffer)) {
            return READERS.read(asset, source, clazz);
        }
    }
}
