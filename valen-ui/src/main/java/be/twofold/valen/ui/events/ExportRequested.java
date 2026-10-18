package be.twofold.valen.ui.events;

import backbonefx.event.*;
import be.twofold.valen.core.game.*;

public record ExportRequested(
    AssetPath path,
    boolean recursive
) implements Event {
}
