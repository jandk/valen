package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.io.*;
import org.jetbrains.annotations.*;

import java.io.*;

public record FlaggedOffset(long offsetAndFlag) {
    private long rawOffset() {
        return offsetAndFlag & 0x00FFFFFFFFFFFFFFL;
    }

    public long offset() {
        return rawOffset() - 1;
    }

    public byte flag() {
        return (byte) ((offsetAndFlag >> 56) & 0xFF);
    }

    public boolean isValid() {
        return rawOffset() != 0;
    }

    public static FlaggedOffset read(BinaryReader reader) throws IOException {
        return new FlaggedOffset(reader.readLong());
    }

    @Override
    public @NotNull String toString() {
        return "FlaggedOffset{" +
            "offset=" + (isValid() ? offset() : 0) + ", " +
            "flag=" + flag() +
            '}';
    }
}
