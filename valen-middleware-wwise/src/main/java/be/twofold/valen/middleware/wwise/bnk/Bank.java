package be.twofold.valen.middleware.wwise.bnk;

import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Bank(
    BankHeader header,
    MediaIndex index,
    long dataOffset,
    int dataSize
) {
    public static Bank read(BinaryReader reader) throws IOException {
        var header = (BankHeader) null;
        var index = MediaIndex.EMPTY;
        var dataOffset = 0L;
        var dataSize = 0;

        while (reader.remaining() > 0) {
            var chunk = ChunkHeader.read(reader);
            var start = reader.position();
            var end = start + chunk.size();

            switch (chunk.tag()) {
                case BKHD -> header = BankHeader.read(reader, chunk.size());
                case DIDX -> index = MediaIndex.read(reader, chunk.size());
                case DATA -> {
                    dataOffset = start;
                    dataSize = chunk.size();
                    reader.skip(chunk.size());
                }
                default -> reader.skip(chunk.size());
            }

            reader.expectPosition(end);
        }

        if (header == null) {
            throw new IOException("Bank has no BKHD chunk");
        }

        return new Bank(header, index, dataOffset, dataSize);
    }

    public long offsetOf(MediaHeader media) {
        return dataOffset + media.offset();
    }
}
