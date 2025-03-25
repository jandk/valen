package be.twofold.valen.ui.component.settings;

sealed interface SettingsViewEvent {
    record Applied() implements SettingsViewEvent {
    }
}
