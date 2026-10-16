package be.twofold.valen.middleware.wwise.pck;

import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record FilePackage(
    FilePackageHeader header,
    LanguageMap languages,
    List<FileEntry<BankId>> soundBanks,
    List<FileEntry<MediaId>> streams,
    List<ExternalEntry> externals
) {
    public static FilePackage read(BinarySource source) throws IOException {
        var header = FilePackageHeader.read(source);
        var languages = LanguageMap.read(source, header.languageMapSize());
        var soundBanks = readTable(source, header.soundBanksLutSize(), FileEntry.BYTES, s -> FileEntry.read(s, BankId::of));
        var streams = readTable(source, header.stmFilesLutSize(), FileEntry.BYTES, s -> FileEntry.read(s, MediaId::of));
        var externals = readTable(source, header.externalsLutSize(), ExternalEntry.BYTES, ExternalEntry::read);

        if (source.position() != header.dataOffset()) {
            throw new IOException("Header ended at " + source.position() + ", expected " + header.dataOffset());
        }

        return new FilePackage(header, languages, soundBanks, streams, externals);
    }

    private static <T> List<T> readTable(
        BinarySource source,
        int size,
        int entrySize,
        BinarySource.Mapper<T> mapper
    ) throws IOException {
        var slice = source.slice(source.position(), size);
        source.skip(size);

        var count = slice.readInt();
        var expected = Integer.BYTES + count * entrySize;
        if (expected != size) {
            throw new IOException(count + " entries need " + expected + " bytes, but there's only " + size);
        }

        var entries = slice.readObjects(count, mapper);
        slice.expectEnd();
        return entries;
    }
}
