package be.twofold.valen.game.doom.mega2;

import wtf.reversed.toolbox.io.*;

import java.nio.file.*;
import java.util.*;

public final class Main {
    public static void main(String[] args) throws Exception {
        var root = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOM\\virtualtextures");
        var files = Files.list(root)
            .filter(p -> p.toString().endsWith(".mega2"))
            .sorted(Comparator.comparingInt(path -> parseId(path.getFileName().toString())))
            .toList();

        for (var file : files) {
            try (var source = BinarySource.open(file)) {
                var mega2 = Mega2.read(source);
                doSomething(source, mega2);
            }
            return;
        }
    }

    private static int parseId(String s) {
        return Integer.parseInt(s, "_vmtr_sq".length(), s.indexOf('.'), 10);
    }

    private static void doSomething(BinarySource source, Mega2 mega2) throws Exception {
        for (var pointer : mega2.pointers()) {
        }

        // var csv = Experiment.toCsv(names, values, Mega2ImageHeader.class);
        // Files.write(out.resolve("..\\mega2.csv"), csv.getBytes());
    }
}
