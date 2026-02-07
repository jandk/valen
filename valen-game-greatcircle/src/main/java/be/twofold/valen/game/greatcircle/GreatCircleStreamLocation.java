package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.io.*;

public record GreatCircleStreamLocation(
    long streamId,
    int size
) implements Location.Custom {
}
