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
        return read(BinaryReader.wrap(Bytes.wrap(in.readAllBytes())));
    }

    public static Codebooks read(BinaryReader reader) throws IOException {
        reader.order(ByteOrder.BIG_ENDIAN);
        reader.expectInt(MAGIC); // magic
        reader.expectShort((short) 0x100); // version
        var libraryCount = reader.readUnsignedShort();
        var codebookCount = reader.readUnsignedShort();

        var libraries = reader.readObjects(libraryCount, CodebookLibrary::read);
        var codebooks = reader.readObjects(codebookCount, Codebook::read);

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
