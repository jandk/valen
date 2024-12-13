package org.redeye.valen.game.halflife.providers;

import be.twofold.valen.core.game.*;
import org.redeye.valen.game.halflife.readers.*;

import java.util.*;

public interface Provider extends Archive {
    default List<Reader<?>> getReaders() {
        return List.of();
    }

    String getName();

    Provider getParent();
}
