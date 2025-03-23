package be.twofold.valen.game.source.readers.vpk;

import be.twofold.valen.core.io.*;

import java.io.*;

public record VpkOtherMD5(
    byte[] treeChecksum,
    byte[] archiveMD5Checksum,
    byte[] wholeFileChecksum
) {
    public static VpkOtherMD5 read(DataSource source) throws IOException {
        var treeChecksum = source.readBytes(16);
        var archiveMD5Checksum = source.readBytes(16);
        var wholeFileChecksum = source.readBytes(16);

        return new VpkOtherMD5(
            treeChecksum,
            archiveMD5Checksum,
            wholeFileChecksum
        );
    }
}
