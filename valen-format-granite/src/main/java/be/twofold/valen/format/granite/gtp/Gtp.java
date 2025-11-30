package be.twofold.valen.format.granite.gtp;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record Gtp(
    GtpHeader header,
    List<GtpPage> pages
) {
    public static Gtp read(BinaryReader reader, int pageSize) throws IOException {
        var header = GtpHeader.read(reader);

        var numPages = Math.ceilDiv(Math.toIntExact(reader.size()), pageSize);
        var pages = new ArrayList<GtpPage>(numPages);
        for (var page = 0; page < numPages; page++) {
            var start = page * pageSize;
            pages.add(GtpPage.read(reader, start));
        }
        return new Gtp(header, List.copyOf(pages));
    }
}
