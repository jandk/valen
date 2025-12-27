package be.twofold.valen.game.source.readers.vpk;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record VpkOtherMD5(
    Bytes treeChecksum,
    Bytes archiveMD5Checksum,
    Bytes wholeFileChecksum
) {
    public static VpkOtherMD5 read(BinarySource source) throws IOException {
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
