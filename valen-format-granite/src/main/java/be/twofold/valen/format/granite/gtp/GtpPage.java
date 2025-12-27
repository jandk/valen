package be.twofold.valen.format.granite.gtp;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record GtpPage(
    List<GtpChunk> chunks
) {
    static GtpPage read(BinarySource source, int position) throws IOException {
        source.position(position == 0 ? GtpHeader.BYTES : position);
        var offsets = source.readInts(source.readInt());

        var chunks = new ArrayList<GtpChunk>(offsets.length());
        for (int i = 0; i < offsets.length(); i++) {
            source.position(position + offsets.get(i));
            chunks.add(GtpChunk.read(source));
        }
        return new GtpPage(List.copyOf(chunks));
    }
}
