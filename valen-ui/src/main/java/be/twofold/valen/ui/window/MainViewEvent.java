package be.twofold.valen.ui.window;

import be.twofold.valen.core.game.*;

sealed interface MainViewEvent {
    record ArchiveSelected(String name) implements MainViewEvent {
    }

    record PathSelected(String name) implements MainViewEvent {
    }

    record AssetSelected(Asset asset) implements MainViewEvent {
    }

    record PreviewVisibilityChanged(boolean visible) implements MainViewEvent {
    }

    record LoadGameClicked() implements MainViewEvent {
    }

    record ExportClicked(Asset asset) implements MainViewEvent {
    }
}
