package be.twofold.valen.game.source.readers.vpk;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record VpkSignature(
    Bytes publicKey,
    Bytes signature
) {
    public static VpkSignature read(BinarySource source) throws IOException {
        Bytes publicKey, signature;
        int publicKeySize = source.readInt();
        if (publicKeySize == VpkHeader.MAGIC) {
            source.expectInt(1);
            publicKeySize = source.readInt();
            int signatureSize = source.readInt();
            source.expectInt(0);

            publicKey = source.readBytes(publicKeySize);
            signature = source.readBytes(signatureSize);
        } else {
            publicKey = source.readBytes(publicKeySize);
            signature = source.readBytes(source.readInt());
        }

        return new VpkSignature(
            publicKey,
            signature
        );
    }
}
