package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.io.*;

public record EternalStreamLocation(
    long streamId,
    int size
) implements Location.Custom {
}
