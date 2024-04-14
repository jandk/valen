package be.twofold.valen;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record Vmtr(
    int version,
    List<VmtrEntry> entries
) {
    public static Vmtr read(BufferedReader reader) throws IOException {
        int version = parseFirst(reader);
        int count = parseFirst(reader);
        reader.readLine(); // skip empty line

        List<VmtrEntry> entries = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            entries.add(VmtrEntry.read(reader));
        }

        return new Vmtr(version, entries);
    }

    private static int parseFirst(BufferedReader reader) throws IOException {
        String s = reader.readLine();
        return Integer.parseInt(s, 0, s.indexOf('\t'), 10);
    }

    public static void main(String[] args) throws Exception {
        var root = Path.of("/home/jan/.local/share/Steam/steamapps/common/DOOM/virtualtextures/");
        var files = Files.list(root)
            .filter(p -> p.toString().endsWith(".vmtr"))
            .toList();

        var entries = new ArrayList<VmtrEntry>();
        for (var file : files) {
            try (var reader = Files.newBufferedReader(file)) {
                var vmtr = Vmtr.read(reader);
                entries.addAll(vmtr.entries());
            }
        }

        var statX = entries.stream().mapToInt(VmtrEntry::x).summaryStatistics();
        var statY = entries.stream().mapToInt(VmtrEntry::y).summaryStatistics();
        var statWidth = entries.stream().mapToInt(VmtrEntry::width).summaryStatistics();
        var statHeight = entries.stream().mapToInt(VmtrEntry::height).summaryStatistics();
        var maxX = entries.stream().mapToInt(e -> e.x() + e.width()).max().orElseThrow();
        var maxY = entries.stream().mapToInt(e -> e.y() + e.height()).max().orElseThrow();

        entries.sort(Comparator
            .comparingInt(VmtrEntry::y)
            .thenComparingInt(VmtrEntry::x));

        var csv = Experiment.toCsv(List.of(), entries, VmtrEntry.class);
        Files.writeString(Path.of("/home/jan/vmtr-entries.csv"), csv);
    }
}
