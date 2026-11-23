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
    public static FilePackage read(BinaryReader reader) throws IOException {
        var header = FilePackageHeader.read(reader);
        var languages = LanguageMap.read(reader, header.languageMapSize());
        var soundBanks = readTable(reader, header.soundBanksLutSize(), FileEntry.BYTES, s -> FileEntry.read(s, BankId::of));
        var streams = readTable(reader, header.stmFilesLutSize(), FileEntry.BYTES, s -> FileEntry.read(s, MediaId::of));
        var externals = readTable(reader, header.externalsLutSize(), ExternalEntry.BYTES, ExternalEntry::read);
        reader.expectPosition(header.dataOffset());

        return new FilePackage(header, languages, soundBanks, streams, externals);
    }

    private static <T> List<T> readTable(
        BinaryReader reader,
        int size,
        int entrySize,
        BinaryReader.Mapper<T> mapper
    ) throws IOException {
        var slice = reader.readSlice(size);
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
