package be.twofold.valen.game.doom;

import be.twofold.valen.game.doom.vmtr.*;

import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        var root = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOM\\virtualtextures");
        var files = Files.list(root)
            .filter(p -> p.toString().endsWith(".vmtr"))
            .toList();

        var entries = new ArrayList<VmtrEntry>();
        for (var file : files) {
            var vmtr = Vmtr.read(file);
            entries.addAll(vmtr.entries());
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

        // var csv = CsvUtils.toCsv(List.of(), entries, VmtrEntry.class);
        // Files.writeString(Path.of("/home/jan/vmtr-entries.csv"), csv);
    }
}
