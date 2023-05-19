package be.twofold.valen.model;

import be.twofold.valen.reader.resource.*;

import java.util.*;

public final class FileEntryMapper {
    private final Resources resources;

    private FileEntryMapper(Resources resources) {
        this.resources = resources;
    }

    public static List<FileEntry> mapEntries(Resources resources) {
        FileEntryMapper mapper = new FileEntryMapper(resources);
        return resources.entries().stream()
            .map(mapper::map)
            .toList();
    }

    private FileEntry map(ResourcesEntry entry) {
        String type = resources.strings().get(entry.pathTupleIndex());
        String name = resources.strings().get(entry.pathTupleIndex() + 1);
        return new FileEntry(
            Name.parse(name),
            type,
            entry.streamResourceHash(),
            entry.dataOffset(),
            entry.dataSize(),
            entry.dataSizeUncompressed(),
            entry.version()
        );
    }
}
