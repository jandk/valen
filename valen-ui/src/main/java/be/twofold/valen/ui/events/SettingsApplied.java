package be.twofold.valen.ui.events;

import backbonefx.event.*;
import be.twofold.valen.ui.common.settings.*;

public record SettingsApplied(
    Settings settings
) implements Event {
}
