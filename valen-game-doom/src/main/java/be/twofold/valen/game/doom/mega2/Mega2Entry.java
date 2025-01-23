package be.twofold.valen.game.doom.mega2;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Mega2Entry(
    long offset,
    int length
) {
    public static Mega2Entry read(DataSource source) throws IOException {
        long offset = source.readLong();
        int length = source.readLongAsInt();

        return new Mega2Entry(
            offset,
            length
        );
    }
}
