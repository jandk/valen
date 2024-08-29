package org.redeye.valen.game.source1.providers;

import be.twofold.valen.core.game.*;
import org.redeye.valen.game.source1.readers.*;

import java.util.*;

public interface Provider extends Archive {
    default List<Reader<?>> getReaders() {
        return List.of(new VtfReader());
    }

    String getName();

    Provider getParent();
}
