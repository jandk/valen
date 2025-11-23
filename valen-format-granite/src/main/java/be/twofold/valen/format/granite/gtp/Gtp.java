package be.twofold.valen.format.granite.gtp;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record Gtp(
    GtpHeader header,
    List<List<GtpChunk>> pages
) {
    public static Gtp read(BinaryReader reader, int pageSize) throws IOException {
        var header = GtpHeader.read(reader);
        var numPages = (reader.size() + pageSize - 1) / pageSize;

        var pages = new ArrayList<List<GtpChunk>>();
        for (int page = 0; page < numPages; page++) {
            int start = page * pageSize;
            reader.position(page == 0 ? 24 : start);
            var offsets = reader.readInts(reader.readInt());

            var chunks = new ArrayList<GtpChunk>();
            for (int offset : offsets) {
                reader.position(start + offset);
                chunks.add(GtpChunk.read(reader));
            }
            pages.add(List.copyOf(chunks));
        }
        return new Gtp(header, List.copyOf(pages));
    }
}
