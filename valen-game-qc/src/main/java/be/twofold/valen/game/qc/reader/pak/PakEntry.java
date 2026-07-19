package be.twofold.valen.game.qc.reader.pak;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.charset.*;

/**
 * This is really just a standard zip CentralDirectoryFileHeader,
 * but with some added encryption.
 */
public record PakEntry(
    short versionMadeBy,
    short versionNeededToExtract,
    short generalPurposeBitFlag,
    short compressionMethod,
    short lastModFileTime,
    short lastModFileDate,
    int crc32,
    long compressedSize,
    long uncompressedSize,
    short fileNameLength,
    short extraFieldLength,
    short fileCommentLength,
    short diskNumberStart,
    short internalFileAttributes,
    int externalFileAttributes,
    long relativeOffsetOfLocalHeader,
    String fileName
) {
    public static final int BYTES = 46;
    public static final int MAGIC = 0x0201_4B50;

    public static PakEntry read(BinarySource source, SaberCipher cipher) throws IOException {
        var buffer = (Bytes.Mutable) source.readBytes(BYTES); // Ew
        cipher.decrypt(buffer);

        if (buffer.getInt(0) != MAGIC) {
            throw new IOException("Invalid magic");
        }

        var versionMadeBy = buffer.getShort(4);
        var versionNeededToExtract = buffer.getShort(6);
        var generalPurposeBitFlag = buffer.getShort(8);
        var compressionMethod = buffer.getShort(10);
        var lastModFileTime = buffer.getShort(12);
        var lastModFileDate = buffer.getShort(14);
        var crc32 = buffer.getInt(16);
        var compressedSize = Integer.toUnsignedLong(buffer.getInt(20));
        var uncompressedSize = Integer.toUnsignedLong(buffer.getInt(24));
        var fileNameLength = buffer.getShort(28);
        var extraFieldLength = buffer.getShort(30);
        var fileCommentLength = buffer.getShort(32);
        var diskNumberStart = buffer.getShort(34);
        var internalFileAttributes = buffer.getShort(36);
        var externalFileAttributes = buffer.getInt(38);
        var relativeOffsetOfLocalHeader = Integer.toUnsignedLong(buffer.getInt(42));

        buffer = (Bytes.Mutable) source.readBytes(fileNameLength); // Ew
        cipher.decrypt(buffer);

        // APPNOTE 4.4.4: Bit 11, filename is UTF-8 rather than CP437
        var charset = (generalPurposeBitFlag & (1 << 11)) != 0
            ? StandardCharsets.UTF_8
            : StandardCharsets.ISO_8859_1;
        var filename = buffer.toString(charset);

        var entry64 = (PakEntry64) null;
        if (extraFieldLength != 0) {
            var slice = source.slice(source.position(), extraFieldLength);
            entry64 = PakEntry64.read(slice, compressedSize, uncompressedSize, relativeOffsetOfLocalHeader);
        }

        source.skip(extraFieldLength + fileCommentLength);

        return new PakEntry(
            versionMadeBy,
            versionNeededToExtract,
            generalPurposeBitFlag,
            compressionMethod,
            lastModFileTime,
            lastModFileDate,
            crc32,
            entry64 == null ? compressedSize : entry64.compressedSize(),
            entry64 == null ? uncompressedSize : entry64.uncompressedSize(),
            fileNameLength,
            extraFieldLength,
            fileCommentLength,
            diskNumberStart,
            internalFileAttributes,
            externalFileAttributes,
            entry64 == null ? relativeOffsetOfLocalHeader : entry64.relativeOffsetOfLocalHeader(),
            filename
        );
    }

    private record PakEntry64(
        long compressedSize,
        long uncompressedSize,
        long relativeOffsetOfLocalHeader
    ) {
        public static PakEntry64 read(
            BinarySource source,
            long compressedSize,
            long uncompressedSize,
            long relativeOffsetOfLocalHeader
        ) throws IOException {
            while (source.remaining() > 0) {
                var headerId = source.readShort();
                var dataSize = source.readShort();
                if (headerId == 0x0001) {
                    if (uncompressedSize == 0xFFFF_FFFFL) {
                        uncompressedSize = source.readLong();
                    }
                    if (compressedSize == 0xFFFF_FFFFL) {
                        compressedSize = source.readLong();
                    }
                    if (relativeOffsetOfLocalHeader == 0xFFFF_FFFFL) {
                        relativeOffsetOfLocalHeader = source.readLong();
                    }
                } else {
                    source.skip(dataSize);
                }
            }

            return new PakEntry64(
                compressedSize,
                uncompressedSize,
                relativeOffsetOfLocalHeader
            );
        }
    }
}
