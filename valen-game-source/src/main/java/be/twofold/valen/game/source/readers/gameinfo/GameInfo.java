package be.twofold.valen.game.source.readers.gameinfo;

import be.twofold.valen.game.source.readers.keyvalue.*;

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
