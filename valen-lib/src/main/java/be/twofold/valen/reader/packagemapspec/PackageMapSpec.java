package be.twofold.valen.reader.packagemapspec;

import java.util.*;

public record PackageMapSpec(
    List<String> files,
    List<String> maps,
    Map<String, List<String>> mapFiles
) {
}
