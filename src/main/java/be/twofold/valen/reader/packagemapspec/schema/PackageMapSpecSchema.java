package be.twofold.valen.reader.packagemapspec.schema;

import java.util.*;

public record PackageMapSpecSchema(
    List<FileSchema> files,
    List<MapFileRefSchema> mapFileRefs,
    List<MapSchema> maps
) {
}
