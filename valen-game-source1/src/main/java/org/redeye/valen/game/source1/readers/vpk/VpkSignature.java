package org.redeye.valen.game.source1.readers.vpk;

import be.twofold.valen.core.io.*;

import java.io.*;

public record VpkSignature(
    byte[] publicKey,
    byte[] signature
) {
    public static VpkSignature read(DataSource source) throws IOException {
        var publicKey = source.readBytes(source.readInt());
        var signature = source.readBytes(source.readInt());

        return new VpkSignature(
            publicKey,
            signature
        );
    }
}
