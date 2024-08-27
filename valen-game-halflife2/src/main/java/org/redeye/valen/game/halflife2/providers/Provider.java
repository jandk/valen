package org.redeye.valen.game.halflife2.providers;

import be.twofold.valen.core.game.*;

public interface Provider extends Archive {
    String getName();

    Provider getParent();
}
