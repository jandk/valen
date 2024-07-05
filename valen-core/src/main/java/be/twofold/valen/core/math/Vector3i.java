package be.twofold.valen.core.math;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Vector3i(int x, int y, int z) {
    public static final int BYTES = 3 * Integer.BYTES;

    public static Vector3i read(DataSource source) throws IOException {
        int x = source.readInt();
        int y = source.readInt();
        int z = source.readInt();
        return new Vector3i(x, y, z);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
