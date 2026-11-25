package be.twofold.valen.middleware.wwise.pck;

import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record FilePackageHeader(
    int headerSize,
    int version,
    int languageMapSize,
    int soundBanksLutSize,
    int stmFilesLutSize,
    int externalsLutSize
) {
    private static final int BYTES = 20;

    public static FilePackageHeader read(BinaryReader reader) throws IOException {
        var chunk = ChunkHeader.read(reader);
        if (chunk.tag() != ChunkTag.AKPK) {
            throw new IOException("Expected AKPK, got " + chunk.tag());
        }

        var version = reader.readInt();
        var languageMapSize = reader.readInt();
        var soundBanksLutSize = reader.readInt();
        var stmFilesLutSize = reader.readInt();
        var externalsLutSize = reader.readInt();

        var partition = BYTES + languageMapSize + soundBanksLutSize + stmFilesLutSize + externalsLutSize;
        if (partition != chunk.size()) {
            throw new IOException("Header does not partition: " + partition + " != " + chunk.size());
        }

        return new FilePackageHeader(
            chunk.size(),
            version,
            languageMapSize,
            soundBanksLutSize,
            stmFilesLutSize,
            externalsLutSize
        );
    }

    public long dataOffset() {
        return ChunkHeader.BYTES + headerSize;
    }
}
