package be.twofold.valen.game.qc.reader.pak;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Pak(
    List<PakEntry> entries
) {
    public static final int SEARCH_WINDOW = 64 * 1024;

    public static Pak read(BinarySource source) throws IOException {
        var footer = PakFooter.read(source);

        var endOfCentralDirectory = findEndOfCentralDirectory(source);
        System.out.println("endOfCentralDirectory = " + endOfCentralDirectory);

        source.position(endOfCentralDirectory.offsetOfCentralDirectory());

        var cipher = new SaberCipher(footer.key());
        var entries = new ArrayList<PakEntry>();
        for (int i = 0; i < endOfCentralDirectory.entryCount(); i++) {
            var header = PakEntry.read(source, cipher);
            entries.add(header);
        }

        return new Pak(entries);
    }

    private static EndOfCentralDirectory findEndOfCentralDirectory(BinarySource source) throws IOException {
        source.position(source.size() - (PakFooter.BYTES + SEARCH_WINDOW));
        Bytes bytes = source.readBytes(SEARCH_WINDOW);

        int index;
        for (index = bytes.length() - 4; index >= 0; index--) {
            if (bytes.getInt(index) == EndOfCentralDirectory.MAGIC) {
                break;
            }
        }

        return EndOfCentralDirectory.read(BinarySource.wrap(bytes.slice(index)));
    }
}
