package be.twofold.valen.game.fear.reader.ltarchive;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.util.*;

public record LTArchive(
    LTArchiveHeader header,
    LTArchiveNames names,
    List<LTArchiveFileEntry> fileEntries,
    List<LTArchiveDirectoryEntry> directoryEntries
) {
    public LTArchive {
        Check.notNull(header);
        Check.notNull(names);
        fileEntries = List.copyOf(fileEntries);
        directoryEntries = List.copyOf(directoryEntries);
    }

    public static LTArchive read(DataSource source) throws IOException {
        var header = LTArchiveHeader.read(source);
        var names = LTArchiveNames.read(source, header.nameSize());
        var fileEntries = source.readStructs(header.numFiles(), LTArchiveFileEntry::read);
        var directoryEntries = source.readStructs(header.numFolders(), LTArchiveDirectoryEntry::read);

        return new LTArchive(
            header,
            names,
            fileEntries,
            directoryEntries
        );
    }
}
