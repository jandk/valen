package be.twofold.valen;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public record Mega2(
    Mega2Header header,
    List<Mega2Level> levels,
    int[] offsets,
    List<Mega2Entry> pointers
) {
    public static Mega2 read(SeekableByteChannel channel) throws IOException {
        var header = IOUtils.readStruct(channel, Mega2Header.BYTES, Mega2Header::read);
        var levels = IOUtils.readStructs(channel, header.levelCount(), Mega2Level.BYTES, Mega2Level::read);

        channel.position(header.quadtreeOffset());
        var offsets = IOUtils.readInts(channel, header.quadtreeCount());

        channel.position(header.pointerOffset());
        var pointers = IOUtils.readStructs(channel, header.pointerCount(), Mega2Entry.BYTES, Mega2Entry::read);

        return new Mega2(
            header,
            levels,
            offsets,
            pointers
        );
    }

    public static void main(String[] args) throws Exception {
        var root = Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOM\\virtualtextures");
        var files = Files.list(root)
            .filter(p -> p.toString().endsWith(".mega2"))
            .sorted(Comparator.comparingInt(path -> parseId(path.getFileName().toString())))
            .toList();

        for (var file : files) {
            try (var channel = Files.newByteChannel(file, StandardOpenOption.READ)) {
                var mega2 = Mega2.read(channel);
                doSomething(mega2, channel);
            }
            return;
        }
    }

    private static void doSomething(Mega2 mega2, SeekableByteChannel channel) throws Exception {
        List<String> names = new ArrayList<>();
        List<Mega2ImageHeader> values = new ArrayList<>();
        Path out = Path.of("D:\\2016\\mega2");
        for (Mega2Entry pointer : mega2.pointers()) {
            channel.position(pointer.offset());
            var buffer = IOUtils.readBuffer(channel, 16);
            Mega2ImageHeader imageHeader = Mega2ImageHeader.read(buffer, pointer.length());
            names.add("%016x.bin".formatted(pointer.offset()));
            values.add(imageHeader);

            channel.position(pointer.offset());
            var data = IOUtils.readBytes(channel, pointer.length());
            String filename = "%08x.bin".formatted(pointer.offset());
            System.out.println(filename);
            Path resolved = out
                .resolve(filename.substring(0, 2))
                .resolve(filename);

            Files.createDirectories(resolved.getParent());
            Files.write(resolved, data);
        }

        var csv = Experiment.toCsv(names, values, Mega2ImageHeader.class);
        Files.write(out.resolve("..\\mega2.csv"), csv.getBytes());
    }

    private static int parseId(String s) {
        return Integer.parseInt(s, "_vmtr_sq".length(), s.indexOf('.'), 10);
    }
}
