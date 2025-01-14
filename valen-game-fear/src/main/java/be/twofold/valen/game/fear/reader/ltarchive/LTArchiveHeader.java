package be.twofold.valen.game.fear.reader.ltarchive;

import be.twofold.valen.core.io.*;

import java.io.*;

public record LTArchiveHeader(
    int nameSize,
    int numFolders,
    int numFiles,
    byte[] hash
) {
    public static LTArchiveHeader read(DataSource source) throws IOException {
        source.expectInt(0x5241544C); // magic
        source.expectInt(3); // version
        var nameSize = source.readInt();
        var numFolders = source.readInt();
        var numFiles = source.readInt();
        source.expectInt(1); // unknown1
        source.expectInt(0); // unknown2
        source.expectInt(1); // unknown3
        var hash = source.readBytes(16);

        return new LTArchiveHeader(
            nameSize,
            numFolders,
            numFiles,
            hash
        );
    }
}
