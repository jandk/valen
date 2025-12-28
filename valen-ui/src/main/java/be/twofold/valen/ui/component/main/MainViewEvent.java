package be.twofold.valen.ui.component.main;

import backbonefx.event.*;

sealed interface MainViewEvent extends Event {
    record ArchiveSelected(String name) implements MainViewEvent {
    }

    record PreviewVisibilityChanged(boolean visible) implements MainViewEvent {
    }

    record SettingVisibilityChanged(boolean visible) implements MainViewEvent {
    }

    record LoadGameClicked() implements MainViewEvent {
    }

    record ExportClicked() implements MainViewEvent {
    }

    record SearchChanged(String query) implements MainViewEvent {
    }
}
