package be.twofold.valen.game.source.readers.vpk;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record VpkArchiveMD5Entry(
    int archiveIndex,
    int startingOffset,
    int count,
    Bytes md5Checksum
) {
    public static VpkArchiveMD5Entry read(BinarySource source) throws IOException {
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
