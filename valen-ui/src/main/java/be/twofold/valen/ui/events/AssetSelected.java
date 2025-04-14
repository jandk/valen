package be.twofold.valen.ui.events;

import backbonefx.event.*;
import be.twofold.valen.core.game.*;

public record AssetSelected(
    Asset asset,
    boolean forced
) implements Event {
}
