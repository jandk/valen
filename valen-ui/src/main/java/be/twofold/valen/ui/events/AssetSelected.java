package be.twofold.valen.ui.events;

import be.twofold.valen.core.game.*;

public record AssetSelected(
    Asset asset,
    boolean forced
) {
}
