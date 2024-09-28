package be.twofold.valen.game.doom.vmtr;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record Vmtr(
    int version,
    List<VmtrEntry> entries
) {
    public static Vmtr read(Path path) throws IOException {
        try (var reader = Files.newBufferedReader(path)) {
            int version = parseFirst(reader);
            int count = parseFirst(reader);
            reader.readLine(); // skip empty line

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