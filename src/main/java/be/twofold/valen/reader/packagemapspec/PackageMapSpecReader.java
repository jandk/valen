package be.twofold.valen.reader.packagemapspec;

import com.google.gson.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class PackageMapSpecReader implements AutoCloseable {
    private final Reader reader;

    private List<String> files;
    private List<String> maps;

    public PackageMapSpecReader(Reader reader) {
        this.reader = reader;
    }

    public PackageMapSpec read() {
        JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
        files = readNames(root.get("files"));
        maps = readNames(root.get("maps"));
        Map<String, List<String>> mapFiles = readMapFiles(root.get("mapFileRefs"));
        return new PackageMapSpec(files, maps, mapFiles);
    }

    private List<String> readNames(JsonElement element) {
        return StreamSupport.stream(element.getAsJsonArray().spliterator(), false)
            .map(e -> e.getAsJsonObject().get("name").getAsString())
            .toList();
    }

    private Map<String, List<String>> readMapFiles(JsonElement element) {
        Map<String, List<String>> mapFiles = new HashMap<>();
        for (JsonElement e : element.getAsJsonArray()) {
            String map = maps.get(e.getAsJsonObject().get("map").getAsInt());
            String file = files.get(e.getAsJsonObject().get("file").getAsInt());
            mapFiles.computeIfAbsent(map, __ -> new ArrayList<>()).add(file);
        }
        mapFiles.replaceAll((k, v) -> List.copyOf(v));
        return Map.copyOf(mapFiles);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
