package org.redeye.valen.game.source1.readers.vpk;

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

        String extension, directory, filename;
        while (!(extension = source.readCString()).isEmpty()) {
            while (!(directory = source.readCString()).isEmpty()) {
                while (!(filename = source.readCString()).isEmpty()) {
                    entries.add(VpkEntry.read(source, extension, directory, filename));
                }
            }
        }
        return new VpkDirectory(entries);
    }
}
