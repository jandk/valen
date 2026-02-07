package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;

public record DarkAgesStreamLocation(
    long streamId,
    int size
) implements Location.Custom {
}
