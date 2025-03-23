package be.twofold.valen.game.source.readers.vpk;

import be.twofold.valen.core.io.*;

import java.io.*;

public record VpkArchiveMD5Entry(
    int archiveIndex,
    int startingOffset,
    int count,
    byte[] md5Checksum
) {
    public static VpkArchiveMD5Entry read(DataSource source) throws IOException {
        var archiveIndex = source.readInt();
        var startingOffset = source.readInt();
        var count = source.readInt();
        var md5Checksum = source.readBytes(16);

        return new VpkArchiveMD5Entry(
            archiveIndex,
            startingOffset,
            count,
            md5Checksum
        );
    }
}
