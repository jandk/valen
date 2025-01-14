package be.twofold.valen.game.fear.reader.ltarchive;

import be.twofold.valen.core.io.*;

import java.io.*;

public record LTArchiveFileEntry(
    int nameOffset,
    long fileOffset,
    long fileLength1,
    long fileLength2
) {
    public static LTArchiveFileEntry read(DataSource source) throws IOException {
        var nameOffset = source.readInt();
        var fileOffset = source.readLong();
        var fileLength1 = source.readLong();
        var fileLength2 = source.readLong();
        source.expectInt(0);

        return new LTArchiveFileEntry(
            nameOffset,
            fileOffset,
            fileLength1,
            fileLength2
        );
    }
}
