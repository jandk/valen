package be.twofold.valen.reader.packagemapspec;

import be.twofold.valen.reader.packagemapspec.schema.*;
import com.fasterxml.jackson.databind.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class PackageMapSpecReader {
    private static final ObjectMapper Mapper = new ObjectMapper();

    public static PackageMapSpec read(Path path) {
        try {
            PackageMapSpecSchema schema = Mapper.readValue(path.toFile(), PackageMapSpecSchema.class);
            return mapSchema(schema);
        } catch (IOException e) {
            System.err.println("Failed to read package map spec: " + path);
            throw new UncheckedIOException(e);
        }
    }

    private static PackageMapSpec mapSchema(PackageMapSpecSchema schema) {
        List<String> files = schema.files().stream()
            .map(FileSchema::name)
            .toList();

        List<String> maps = schema.maps().stream()
            .map(MapSchema::name)
            .toList();

        Map<String, List<String>> mapFiles = schema.mapFileRefs().stream()
            .collect(Collectors.groupingBy(
                ref -> maps.get(ref.map()),
                Collectors.mapping(
                    ref -> files.get(ref.file()),
                    Collectors.toUnmodifiableList()
                )
            ));

        return new PackageMapSpec(files, maps, Map.copyOf(mapFiles));
    }

}
