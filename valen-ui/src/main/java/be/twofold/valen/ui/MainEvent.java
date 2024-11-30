package be.twofold.valen.ui;

public sealed interface MainEvent {
    record GameLoadRequested() implements MainEvent {
    }
}
