package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;

public record GreatCircleStreamLocation(
    long streamId,
    int size
) implements StorageLocation.Custom {
}
