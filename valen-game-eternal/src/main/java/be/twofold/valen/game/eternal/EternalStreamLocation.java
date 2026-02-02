package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;

public record EternalStreamLocation(
    long streamId,
    int size
) implements StorageLocation.Custom {
}
