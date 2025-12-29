package be.twofold.valen.game.goldsrc.reader.wad;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Wad(
    WadHeader header,
    List<WadEntry> entries
) {
    public static Wad read(BinarySource source) throws IOException {
        var header = WadHeader.read(source);

        source.position(header.entryOffset());
        var entries = source.readObjects(header.entryCount(), WadEntry::read);

        return new Wad(
            header,
            entries
        );
    }
}
