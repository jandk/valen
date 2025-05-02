package be.twofold.valen.game.doom.mega2;

import be.twofold.valen.core.io.*;

import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        var root = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOM\\virtualtextures");
        var files = Files.list(root)
            .filter(p -> p.toString().endsWith(".mega2"))
            .sorted(Comparator.comparingInt(path -> parseId(path.getFileName().toString())))
            .toList();

        for (var file : files) {
            var mega2 = Mega2.read(file);
            try (var source = DataSource.fromPath(file)) {
                doSomething(mega2, source);
            }
            return;
        }
    }

    private static int parseId(String s) {
        return Integer.parseInt(s, "_vmtr_sq".length(), s.indexOf('.'), 10);
    }

    private static void doSomething(Mega2 mega2, DataSource source) throws Exception {
        var names = new ArrayList<String>();
        var values = new ArrayList<Mega2ImageHeader>();
        var out = Path.of("D:\\2016\\mega2");
        for (var pointer : mega2.pointers()) {
            source.position(pointer.offset());
            var imageHeader = Mega2ImageHeader.read(source, pointer.length());
            names.add("%016x.bin".formatted(pointer.offset()));
            values.add(imageHeader);

            source.position(pointer.offset());
            var data = source.readBytes(pointer.length());
            var filename = "%08x.bin".formatted(pointer.offset());
            System.out.println(filename);
            var resolved = out
                .resolve(filename.substring(0, 2))
                .resolve(filename);

            Files.createDirectories(resolved.getParent());
            Files.write(resolved, data);
        }

        // var csv = Experiment.toCsv(names, values, Mega2ImageHeader.class);
        // Files.write(out.resolve("..\\mega2.csv"), csv.getBytes());
    }
}
