package be.twofold.valen.game.doom.megatexture.vmtr;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * A {@code .vmtr} manifest, listing every material packed into the virtual texture atlas.
 */
public record Vmtr(
    int version,
    List<VmtrEntry> entries
) {
    public static List<VmtrEntry> readAll(Path directory) throws IOException {
        List<Path> paths;
        try (var stream = Files.list(directory)) {
            paths = stream
                .filter(path -> path.getFileName().toString().endsWith(".vmtr"))
                .sorted()
                .toList();
        }

        var entries = new ArrayList<VmtrEntry>();
        for (var path : paths) {
            entries.addAll(read(path).entries());
        }
        return List.copyOf(entries);
    }

    public static Vmtr read(Path path) throws IOException {
        try (var reader = Files.newBufferedReader(path)) {
            int version = parseFirst(reader);
            int count = parseFirst(reader);
            reader.readLine(); // skip the column header

            List<VmtrEntry> entries = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                entries.add(VmtrEntry.read(reader));
            }

            return new Vmtr(version, entries);
        }
    }

    private static int parseFirst(BufferedReader reader) throws IOException {
        String s = reader.readLine();
        return Integer.parseInt(s, 0, s.indexOf('\t'), 10);
    }
}
