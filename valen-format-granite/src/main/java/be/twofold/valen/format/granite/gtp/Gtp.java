package be.twofold.valen.format.granite.gtp;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record Gtp(
    GtpHeader header,
    List<GtpPage> pages
) {
    public static Gtp read(BinarySource source, int pageSize) throws IOException {
        var header = GtpHeader.read(source);

        var numPages = Math.ceilDiv(Math.toIntExact(source.size()), pageSize);
        var pages = new ArrayList<GtpPage>(numPages);
        for (var page = 0; page < numPages; page++) {
            var start = page * pageSize;
            pages.add(GtpPage.read(source, start));
        }
        return new Gtp(header, List.copyOf(pages));
    }
}
