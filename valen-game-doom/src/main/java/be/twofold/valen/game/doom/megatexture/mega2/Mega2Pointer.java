package be.twofold.valen.game.doom.megatexture.mega2;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Mega2Pointer(
    long offset,
    int length
) {
    public static Mega2Pointer read(BinarySource source) throws IOException {
        long offset = source.readLong();
        int length = source.readLongAsInt();

        return new Mega2Pointer(
            offset,
            length
        );
    }
}
