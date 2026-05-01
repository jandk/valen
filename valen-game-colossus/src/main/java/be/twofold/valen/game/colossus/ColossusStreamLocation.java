package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;

public record ColossusStreamLocation(
    long streamId,
    int compressedSize,
    int uncompressedSize,
    int tier
) implements Location.Custom {
    public ColossusStreamLocation(long streamId, int compressedSize, int uncompressedSize) {
        this(streamId, compressedSize, uncompressedSize, 0);
    }

    @Override
    public int size() {
        return uncompressedSize;
    }
}
