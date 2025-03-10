package org.redeye.valen.game.source1.readers.gameinfo;

import org.redeye.valen.game.source1.readers.keyvalue.*;

import java.io.*;
import java.util.*;

public record GameInfoFileSystem(
    int steamAppId,
    List<Map.Entry<String, String>> searchPaths
) {
    public GameInfoFileSystem {
        searchPaths = List.copyOf(searchPaths);
    }

    public static GameInfoFileSystem read(KeyValue.Obj fileSystem) throws IOException {
        var steamAppId = Integer.parseInt(fileSystem.getString("SteamAppId"));
        var rawSearchPaths = fileSystem.getObject("SearchPaths");

        var searchPaths = new ArrayList<Map.Entry<String, String>>();
        for (var entry : rawSearchPaths.values()) {
            if (!(entry.getValue() instanceof KeyValue.Str(var value))) {
                throw new IOException("Expected a string");
            }

            for (var key : entry.getKey().split("\\+")) {
                searchPaths.add(Map.entry(key, value));
            }
        }
        return new GameInfoFileSystem(steamAppId, searchPaths);
    }
}
