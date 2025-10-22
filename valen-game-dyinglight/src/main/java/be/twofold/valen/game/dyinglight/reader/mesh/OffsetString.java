package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.io.*;

import java.io.*;

public class OffsetString {
    public static String read(BinaryReader reader, boolean canBeEmbedded) throws IOException {
        var offset = FlaggedOffset.read(reader);
        if (canBeEmbedded && offset.flag() == 0x00) {
            // Embeddable string (8 bytes) should have flag 0x00 when it's embedded
            // The string is stored directly in the 8 bytes of the offset field
            var currentPosition = reader.position();
            reader.position(currentPosition - 8);
            var string = reader.readCString();
            reader.position(currentPosition + 8);
            return string;

        } else if (canBeEmbedded && offset.flag() != 0x21) {
            // Embeddable string should have flag 0x21 when it's a real offset
            throw new IOException("Unexpected flag for string: " + offset.flag());
        }
        if (!offset.isValid()) {
            return "";
        }
        var currentPosition = reader.position();
        reader.position(offset.offset());
        var result = reader.readCString();
        reader.position(currentPosition);
        return result;
    }
}
