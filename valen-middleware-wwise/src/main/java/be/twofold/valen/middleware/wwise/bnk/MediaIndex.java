package be.twofold.valen.middleware.wwise.bnk;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record MediaIndex(
    List<MediaHeader> headers
) {
    public static final MediaIndex EMPTY = new MediaIndex(List.of());

    public MediaIndex {
        headers = List.copyOf(headers);
    }

    public static MediaIndex read(BinarySource source, int size) throws IOException {
        if (size % MediaHeader.BYTES != 0) {
            throw new IOException("Invalid size for MediaIndex");
        }

        var headers = source.readObjects(size / MediaHeader.BYTES, MediaHeader::read);
        return new MediaIndex(headers);
    }
}
