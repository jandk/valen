package org.redeye.valen.game.source1.providers;

import be.twofold.valen.core.game.*;
import org.redeye.valen.game.source1.utils.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;

public class GameinfoProvider implements Provider {
    private final Path path;
    private final List<Provider> providers = new ArrayList<>();

    public GameinfoProvider(Path path) throws IOException {
        this.path = path;
        var gameinfoData = new ValveKeyValueParser();

        try {
            gameinfoData.parseFile(path.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var rawSearchPaths = (Map<String, List<Object>>) gameinfoData.getValueByPath("gameinfo.filesystem.searchpaths");
        var parsedSearchPaths = parseSearchPaths(rawSearchPaths, path.getParent().toString(), path.getParent().getParent().toString());
        var searchPaths = new LinkedHashSet<Path>();
        searchPaths.addAll(parsedSearchPaths.get("game"));
        searchPaths.addAll(parsedSearchPaths.get("mod"));
        searchPaths.addAll(parsedSearchPaths.get("platform"));
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

    /**
     * Parses the SearchPaths block from gameinfo.txt, replaces placeholders, converts paths to absolute paths,
     * and handles wildcards by expanding directories.
     *
     * @param searchPathsMap A map representing the SearchPaths block.
     * @param gameinfoPath   The actual value to replace |gameinfo_path|.
     * @param gameRoot       The root directory of the game to replace |all_source_engine_paths|.
     * @return A structured map with path types as keys and lists of absolute paths as values.
     */
    public static Map<String, List<Path>> parseSearchPaths(Map<String, List<Object>> searchPathsMap, String gameinfoPath, String gameRoot) {
        Map<String, List<Path>> parsedPaths = new HashMap<>();
        if (!(gameinfoPath.endsWith("/") | gameinfoPath.endsWith("\\"))) {
            gameinfoPath += "/";
        }
        if (!(gameRoot.endsWith("/") | gameRoot.endsWith("\\"))) {
            gameRoot += "/";
        }
        for (Map.Entry<String, List<Object>> entry : searchPathsMap.entrySet()) {
            String key = entry.getKey(); // e.g., "game", "mod", etc.
            List<Object> values = entry.getValue();

            List<Path> paths = new ArrayList<>();
            for (Object value : values) {
                if (value instanceof String) {
                    // Split the value by commas and trim each path
                    String[] splitPaths = ((String) value).split(",");
                    for (String path : splitPaths) {
                        path = path.trim();

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

                        // Convert the path to an absolute Path object

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
                    }
                }
            }
            parsedPaths.put(key, paths);
        }

        return parsedPaths;
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
