package be.twofold.valen;

import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

public record ResourceIndex2016(
    ResourceIndex2016Header header,
    List<ResourceIndex2016Entry> entries
) {
    public static ResourceIndex2016 read(BetterBuffer buffer) {
        var header = ResourceIndex2016Header.read(buffer);
        var numEntries = Integer.reverseBytes(buffer.getInt());
        var entries = buffer.getStructs(numEntries, bb -> ResourceIndex2016Entry.read(bb, header));

        return new ResourceIndex2016(header, entries);
    }

    public static void main(String[] args) throws Exception {
        String path = "D:\\Games\\Steam\\steamapps\\common\\DOOM\\base\\gameresources.index";
        var buffer = BetterBuffer.wrap(Files.readAllBytes(Paths.get(path)));

        var index = ResourceIndex2016.read(buffer);
//        var csv = Experiment.toCsv(List.of(), index.entries, ResourceIndex2016Entry.class);
//        Files.writeString(Paths.get("D:\\gameresources.csv"), csv);

        try (var channel = Files.newByteChannel(Path.of("D:\\Games\\Steam\\steamapps\\common\\DOOM\\base\\gameresources.resources"))) {
            for (var entry : index.entries) {
                if (entry.name3().isEmpty() || entry.patchFileNumber() != 0) {
                    continue;
                }
                channel.position(entry.offset());
                var data = IOUtils.readBytes(channel, entry.sizeCompressed());
                byte[] deco;
                if (entry.size() != entry.sizeCompressed()) {
                    deco = new byte[entry.size()];
                    try (var inflater = new InflaterInputStream(new ByteArrayInputStream(data), new Inflater(true))) {
                        inflater.readNBytes(deco, 0, deco.length);
                    }
                } else {
                    deco = data;
                }
                var dest = Path.of("D:\\2016\\resources").resolve(entry.name3());
                System.out.println(dest);

                Files.createDirectories(dest.getParent());
                Files.write(dest, deco);
            }
        }

        System.out.println("Header: " + index.header);
        System.out.println("Entries: " + index.entries.size());
    }

    @Override
    public String toString() {
        return "ResourceIndex2016(" +
               "header=" + header + ", " +
               "entries=[" + entries.size() + "]" +
               ")";
    }
}
