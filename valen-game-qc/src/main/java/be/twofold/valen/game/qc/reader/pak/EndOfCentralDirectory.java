package be.twofold.valen.game.qc.reader.pak;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

/**
 * This is not a standard EOCD as found in zip files.
 * This is a custom implementations with a bunch of fields missing.
 */
record EndOfCentralDirectory(
    boolean wideValues,
    long entryCount,
    long sizeOfCentralDirectory,
    long offsetOfCentralDirectory,
    long checksum,
    int commentLength
) {
    static final int MAGIC = 0x0606_4B50;

    static EndOfCentralDirectory read(BinarySource source) throws IOException {
        source.expectInt(MAGIC);
        var wideValues = source.readBool(BoolFormat.BYTE);
        var totalNumberOfEntries = source.readLong();
        var sizeOfCentralDirectory = source.readLong();
        var offsetOfCentralDirectory = source.readLong();
        var checksum = source.readLong();
        var commentLength = source.readInt();

        var actualChecksum = totalNumberOfEntries ^ (offsetOfCentralDirectory + sizeOfCentralDirectory);
        Check.state(checksum == actualChecksum, "Checksum does not match");

        return new EndOfCentralDirectory(
            wideValues,
            totalNumberOfEntries,
            sizeOfCentralDirectory,
            offsetOfCentralDirectory,
            checksum,
            commentLength
        );
    }
}
