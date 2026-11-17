package be.twofold.valen.core.game;

import java.nio.file.*;

public record GameEntry(
    String name,
    Path executable
) {
}
