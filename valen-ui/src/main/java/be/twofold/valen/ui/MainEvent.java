package be.twofold.valen.ui;

import backbonefx.event.*;

import java.nio.file.*;
import java.util.function.*;

public sealed interface MainEvent extends Event {
    record GameLoadRequested() implements MainEvent {
    }

    record SaveFileRequested(
        String filename,
        Consumer<Path> consumer
    ) implements MainEvent {
    }
}
