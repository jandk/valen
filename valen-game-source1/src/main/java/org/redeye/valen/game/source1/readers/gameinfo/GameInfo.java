package org.redeye.valen.game.source1.readers.gameinfo;

import org.redeye.valen.game.source1.readers.keyvalue.*;

import java.io.*;
import java.nio.file.*;

public record GameInfo(
    String game,
    String title,
    GameInfoFileSystem fileSystem
) {
    public static GameInfo read(Path path) throws IOException {
        var kv = KeyValue
            .parse(Files.readString(path))
            .getObject("GameInfo");
        var game = kv.getString("game");
        var title = kv.getString("title");
        var fileSystem = GameInfoFileSystem.read(kv.getObject("FileSystem"));
        return new GameInfo(game, title, fileSystem);
    }
}
