package be.twofold.valen.q3bsp.model;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Visibility(
    int nVecs,
    int szVecs,
    byte[] data
) {
    public static Visibility read(DataSource source) throws IOException {
        int nVecs = source.readInt();
        int szVecs = source.readInt();
        byte[] data = source.readBytes(nVecs * szVecs);

        return new Visibility(nVecs, szVecs, data);
    }
}
