package org.redeye.valen.game.source1.readers.vpk;

import be.twofold.valen.core.io.*;

import java.io.*;

public record VpkHeader(
    VpkVersion version,
    int treeSize,
    int fileDataSize,
    int archiveMD5Size,
    int otherMD5Size,
    int signatureSize
) {
    public static VpkHeader read(DataSource source) throws IOException {
        source.expectInt(0x55AA1234);
        var version = VpkVersion.fromValue(source.readInt());
        var treeSize = source.readInt();

        int fileDataSize = 0;
        int archiveMD5Size = 0;
        int otherMD5Size = 0;
        int signatureSize = 0;
        if (version == VpkVersion.TWO) {
            fileDataSize = source.readInt();
            archiveMD5Size = source.readInt();
            otherMD5Size = source.readInt();
            signatureSize = source.readInt();
        }

        return new VpkHeader(
            version,
            treeSize,
            fileDataSize,
            archiveMD5Size,
            otherMD5Size,
            signatureSize
        );
    }

    public int size() {
        return version().size();
    }
}
