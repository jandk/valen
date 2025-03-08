package org.redeye.valen.game.source1.vpk;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

record VpkDirectory(
    List<VpkEntry> entries
) {
    VpkDirectory {
        entries = List.copyOf(entries);
    }

    static VpkDirectory read(DataSource source) throws IOException {
        source.expectInt(0x55AA1234);
        source.expectInt(2); // Only version 2 for now
        source.skip(20); // eeh, fuck it

        var entries = new ArrayList<VpkEntry>();
        while (true) {
            var extension = source.readCString();
            if (extension.isEmpty()) {
                break;
            }

            while (true) {
                var directory = source.readCString();
                if (directory.isEmpty()) {
                    break;
                }

                while (true) {
                    var filename = source.readCString();
                    if (filename.isEmpty()) {
                        break;
                    }

                    int crc = source.readInt();
                    short preloadBytes = source.readShort();
                    short archiveIndex = source.readShort();
                    int entryOffset = source.readInt();
                    int entryLength = source.readInt();
                    source.expectShort((short) 0xFFFF);

                    var fullName = (directory.isBlank() ? "" : directory + "/")
                        + filename
                        + (extension.isBlank() ? "" : "." + extension);

                    var entry = new VpkEntry(
                        fullName,
                        crc,
                        preloadBytes,
                        archiveIndex,
                        entryOffset,
                        entryLength
                    );

                    entries.add(entry);
                }
            }
        }
        return new VpkDirectory(entries);
    }
}
