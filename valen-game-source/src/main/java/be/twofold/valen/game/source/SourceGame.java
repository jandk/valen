package be.twofold.valen.game.source;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.source.index.*;
import be.twofold.valen.game.source.readers.gameinfo.*;
import be.twofold.valen.game.source.readers.keyvalue.*;
import be.twofold.valen.game.source.readers.vmt.*;
import be.twofold.valen.game.source.readers.vtf.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class SourceGame implements Game {
    private static final List<AssetReader<?, SourceAsset>> READERS = List.of(
        new KeyValueReader(),
        new VmtReader(),
        new VtfReader()
    );

    private final Path path;
    private final List<String> mods;

    public SourceGame(Path path, List<String> mods) {
        this.path = Check.nonNull(path, "path");
        this.mods = List.copyOf(mods);
    }

    @Override
    public List<String> archiveNames() {
        return mods;
    }

    @Override
    public AssetLoader open(String name) throws IOException {
        var modRoot = path.resolve(name);
        var gameRoot = modRoot.getParent();

        var wanted = Set.of("game", "mod", "platform");
        var gameInfo = GameInfo.read(modRoot.resolve("gameinfo.txt"));
        var searchPaths = gameInfo.fileSystem().searchPaths().stream()
            .filter(e -> wanted.contains(e.getKey()))
            .flatMap(e -> parseSearchPath(e.getValue(), gameRoot, modRoot).stream())
            .distinct()
            .toList();

        var sources = new HashMap<FileId, BinarySource>();
        var index = new HashMap<SourceAssetID, SourceAsset>();
        for (var searchPath : searchPaths) {
            if (searchPath.getFileName().toString().endsWith(".vpk")) {
                if (Files.notExists(searchPath)) {
                    var filename = searchPath.getFileName().toString();
                    filename = filename.substring(0, filename.length() - 4);
                    searchPath = searchPath.getParent().resolve(filename + "_dir.vpk");
                }
                if (Files.exists(searchPath)) {
                    VpkIndex vpkIndex = VpkIndex.build(searchPath);
                    sources.putAll(vpkIndex.sources());
                    index.putAll(vpkIndex.index());
                }
            } else {
                index.putAll(FolderIndex.build(searchPath).index());
            }
        }

        var archive = new SourceArchive(Map.copyOf(index));
        var storageManager = new StorageManager(sources, Set.of());
        return new AssetLoader(archive, storageManager, List.copyOf(READERS));
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
}
