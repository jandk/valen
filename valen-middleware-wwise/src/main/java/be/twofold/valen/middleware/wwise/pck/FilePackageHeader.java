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

    public static FilePackageHeader read(BinarySource source) throws IOException {
        var chunk = ChunkHeader.read(source);
        if (chunk.tag() != ChunkTag.AKPK) {
            throw new IOException("Expected AKPK, got " + chunk.tag());
        }

        var version = source.readInt();
        var languageMapSize = source.readInt();
        var soundBanksLutSize = source.readInt();
        var stmFilesLutSize = source.readInt();
        var externalsLutSize = source.readInt();

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
