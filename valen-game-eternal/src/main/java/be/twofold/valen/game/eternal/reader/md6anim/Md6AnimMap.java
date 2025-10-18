package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.util.collect.*;

public record Md6AnimMap(
    short tableCRC,
    Ints constR,
    Ints constS,
    Ints constT,
    Ints constU,
    Ints animR,
    Ints animS,
    Ints animT,
    Ints animU
) {
}
