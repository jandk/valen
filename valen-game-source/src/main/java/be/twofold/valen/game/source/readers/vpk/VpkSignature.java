package be.twofold.valen.game.source.readers.vpk;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record VpkSignature(
    Bytes publicKey,
    Bytes signature
) {
    public static VpkSignature read(BinarySource source) throws IOException {
        var publicKey = source.readBytes(source.readInt());
        var signature = source.readBytes(source.readInt());

        return new VpkSignature(
            publicKey,
            signature
        );
    }
}
