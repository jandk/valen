package org.redeye.valen.game.source1.providers;

import be.twofold.valen.core.game.*;
import org.redeye.valen.game.source1.utils.keyvalues.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class GameinfoProvider implements Provider {
    private final Path path;
    private final List<Provider> providers = new ArrayList<>();

    public GameinfoProvider(Path path) throws IOException {
        this.path = path;
        String modRoot = path.getParent().toString();
        String gameRoot = path.getParent().getParent().toString();
        var vdfReader = new VdfReader(new FileReader(path.toFile()));
        var gameinfoData = vdfReader.parse();

        VdfPath lookupPath = VdfPath.of("gameinfo.filesystem.searchpaths.[0]");
        var rawSearchPaths = lookupPath.lookup(gameinfoData).orElseThrow().asObject();
        var searchPaths = Stream.of("game", "mod", "platform")
            .flatMap(s -> rawSearchPaths.get(s).asArray().stream())
            .filter(Objects::nonNull)
            .flatMap(v -> parseSearchPath(v.asString(), modRoot, gameRoot).stream())
            .collect(Collectors.toCollection(LinkedHashSet::new));

        for (Path searchPath : searchPaths) {
            if (searchPath.getFileName().toString().endsWith(".vpk")) {
                if (Files.notExists(searchPath)) {
                    var name = searchPath.getFileName().toString();
                    name = name.substring(0, name.length() - 4);
                    searchPath = searchPath.getParent().resolve(name + "_dir.vpk");
                }
                if (Files.exists(searchPath)) {
                    providers.add(new VpkArchive(searchPath, this));
                }
            } else {
                providers.add(new FolderProvider(searchPath, this));
            }
        }
    }

    public static List<Path> parseSearchPath(String searchPath, String gameinfoPath, String gameRoot) {
        if (searchPath == null || searchPath.isEmpty()) {
            return Collections.emptyList();
        }
        if (!(gameinfoPath.endsWith("/") || gameinfoPath.endsWith("\\"))) {
            gameinfoPath += "/";
        }
        if (!(gameRoot.endsWith("/") || gameRoot.endsWith("\\"))) {
            gameRoot += "/";
        }
        var path = searchPath.trim();
        var paths = new ArrayList<Path>();
        // Replace placeholders with actual values
        if (path.contains("|gameinfo_path|")) {
            path = path.replace("|gameinfo_path|", gameinfoPath);
        } else if (path.contains("|all_source_engine_paths|")) {
            path = path.replace("|all_source_engine_paths|", gameRoot);
        }
        // If the path has no placeholders, assume it's relative to the game root
        else if (!path.contains("|")) {
            path = gameRoot + "/" + path;
        }

        // Handle directories that need expansion (where the path ends with a directory containing "*")
        if (path.endsWith("*")) {
            Path directory = Path.of(path.substring(0, path.length() - 1));
            if (Files.isDirectory(directory)) {
                paths.addAll(expandDirectory(directory));
            }
        } else {
            if (path.endsWith(".")) {
                path = path.substring(0, path.length() - 1);
            }
            paths.add(Paths.get(path).toAbsolutePath());
        }
        return paths;
    }

    /**
     * Expands the contents of a directory into a list of paths.
     *
     * @param directory The directory to expand.
     * @return A list of paths within the directory.
     */
    private static List<Path> expandDirectory(Path directory) {
        List<Path> expandedPaths = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path entry : stream) {
                expandedPaths.add(entry.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return expandedPaths;
    }

    @Override
    public String getName() {
        return this.path.getParent().getFileName().toString();
    }

    @Override
    public Provider getParent() {
        return this;
    }

    @Override
    public List<Asset> assets() {
        final List<Asset> allAssets = new ArrayList<>();
        for (Provider provider : providers) {
            allAssets.addAll(provider.assets());
        }
        return allAssets;
    }

    @Override
    public boolean exists(AssetID identifier) {
        return providers.stream().anyMatch(provider -> provider.exists(identifier));
    }

    @Override
    public Object loadAsset(AssetID identifier) throws IOException {
        for (Provider provider : providers) {
            if (provider.exists(identifier)) {
                return provider.loadAsset(identifier);
            }
        }
        return null;
    }

    @Override
    public ByteBuffer loadRawAsset(AssetID identifier) throws IOException {
        for (Provider provider : providers) {
            if (provider.exists(identifier)) {
                return provider.loadRawAsset(identifier);
            }
        }
        return null;
    }
}
