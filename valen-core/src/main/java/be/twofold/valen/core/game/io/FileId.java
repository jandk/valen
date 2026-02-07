package be.twofold.valen.core.game.io;

import org.jetbrains.annotations.*;
import wtf.reversed.toolbox.util.*;

public record FileId(
    String name
) {
    public FileId {
        Check.nonNull(name, "name");
    }

    @Override
    public @NotNull String toString() {
        return name;
    }
}
