package be.twofold.valen.game.darkages.reader.packagemapspec;

import java.util.*;
import java.util.stream.*;

public record PackageMapSpec(
    List<String> files,
    List<String> maps,
    Map<String, List<String>> mapFiles
) {
    public PackageMapSpec {
        files = List.copyOf(files);
        maps = List.copyOf(maps);
        mapFiles = mapFiles.entrySet().stream()
            .collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey,
                e -> List.copyOf(e.getValue())
            ));
    }
}
