package be.twofold.valen.game.fear.reader.ltarchive;

import be.twofold.valen.core.io.*;

import java.io.*;

public record LTArchiveDirectoryEntry(
    int nameOffset,
    int subFolders,
    int nextFolders,
    int numFiles
) {
    public static LTArchiveDirectoryEntry read(DataSource source) throws IOException {
        var nameOffset = source.readInt();
        var subFolders = source.readInt();
        var nextFolders = source.readInt();
        var numFiles = source.readInt();

        return new LTArchiveDirectoryEntry(
            nameOffset,
            subFolders,
            nextFolders,
            numFiles
        );
    }
}
