package be.twofold.valen.ui.component.main;

sealed interface MainViewEvent {
    record ArchiveSelected(String name) implements MainViewEvent {
    }

    record PreviewVisibilityChanged(boolean visible) implements MainViewEvent {
    }

    record LoadGameClicked() implements MainViewEvent {
    }

    record ExportClicked() implements MainViewEvent {
    }
}
