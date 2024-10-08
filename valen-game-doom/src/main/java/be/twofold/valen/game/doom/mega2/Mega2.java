package be.twofold.valen.game.doom.mega2;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record Mega2(
    Mega2Header header,
    List<Mega2Level> levels,
    int[] offsets,
    List<Mega2Entry> pointers
) {
    public static Mega2 read(Path path) throws IOException {
        try (var source = DataSource.fromPath(path)) {
            var header = Mega2Header.read(source);
            var levels = source.readStructs(header.quadtreeLevelCount(), Mega2Level::read);

            source.seek(header.quadtreeOffset());
            var offsets = source.readInts(header.quadtreeCount());

            source.seek(header.pointerOffset());
            var pointers = source.readStructs(header.pointerCount(), Mega2Entry::read);

            return new Mega2(
                header,
                levels,
                offsets,
                pointers
            );
        }
    }
}
