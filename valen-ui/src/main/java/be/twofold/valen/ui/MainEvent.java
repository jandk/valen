package be.twofold.valen.ui;

import java.nio.file.*;
import java.util.function.*;

public sealed interface MainEvent {
    record GameLoadRequested() implements MainEvent {
    }

    record SaveFileRequested(
        String filename,
        Consumer<Path> consumer
    ) implements MainEvent {
    }
}
