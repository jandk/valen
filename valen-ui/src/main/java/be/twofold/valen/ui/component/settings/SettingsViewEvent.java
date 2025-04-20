package be.twofold.valen.ui.component.settings;

import backbonefx.event.*;

sealed interface SettingsViewEvent extends Event {
    record Applied() implements SettingsViewEvent {
    }
}
