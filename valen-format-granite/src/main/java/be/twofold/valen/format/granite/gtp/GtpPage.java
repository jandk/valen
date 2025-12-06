package be.twofold.valen.format.granite.gtp;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record GtpPage(
    List<GtpChunk> chunks
) {
    static GtpPage read(BinaryReader reader, int position) throws IOException {
        reader.position(position == 0 ? GtpHeader.BYTES : position);
        var offsets = reader.readInts(reader.readInt());

        var chunks = new ArrayList<GtpChunk>(offsets.length());
        for (int i = 0; i < offsets.length(); i++) {
            reader.position(position + offsets.get(i));
            chunks.add(GtpChunk.read(reader));
        }
        return new GtpPage(List.copyOf(chunks));
    }
}
