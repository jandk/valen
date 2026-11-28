package be.twofold.valen.game.doom.megatexture.mega2;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Mega2Pointer(
    long offset,
    int length
) {
    public static Mega2Pointer read(BinaryReader reader) throws IOException {
        long offset = reader.readLong();
        int length = reader.readLongAsInt();

        return new Mega2Pointer(
            offset,
            length
        );
    }
}
