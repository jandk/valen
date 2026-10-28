package be.twofold.valen.middleware.wwise.codebook;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record Codebooks(
    List<CodebookLibrary> libraries,
    List<Codebook> codebooks
) {
    private static final int MAGIC = 'C' << 24 | 'D' << 16 | 'B' << 8 | 'K';

    public static Codebooks load(InputStream in) throws IOException {
        return read(BinarySource.wrap(Bytes.wrap(in.readAllBytes())));
    }

    public static Codebooks read(BinarySource source) throws IOException {
        source.order(ByteOrder.BIG_ENDIAN);
        source.expectInt(MAGIC); // magic
        source.expectShort((short) 0x100); // version
        var libraryCount = Short.toUnsignedInt(source.readShort());
        var codebookCount = Short.toUnsignedInt(source.readShort());

        var libraries = source.readObjects(libraryCount, CodebookLibrary::read);
        var codebooks = source.readObjects(codebookCount, Codebook::read);

        return new Codebooks(
            libraries,
            codebooks
        );
    }

    public List<Codebook> library(String name) {
        var library = libraries.stream()
            .filter(l -> l.name().equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown library: " + name));

        return library.indices().stream()
            .mapToObj(codebooks::get)
            .toList();
    }
}
