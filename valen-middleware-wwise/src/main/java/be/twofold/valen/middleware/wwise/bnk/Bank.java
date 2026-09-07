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
    public static Bank read(BinarySource source) throws IOException {
        var header = (BankHeader) null;
        var index = MediaIndex.EMPTY;
        var dataOffset = 0L;
        var dataSize = 0;

        while (source.remaining() > 0) {
            var chunk = ChunkHeader.read(source);
            var start = source.position();
            var end = start + chunk.size();

            switch (chunk.tag()) {
                case BKHD -> header = BankHeader.read(source, chunk.size());
                case DIDX -> index = MediaIndex.read(source, chunk.size());
                case DATA -> {
                    dataOffset = start;
                    dataSize = chunk.size();
                    source.skip(chunk.size());
                }
                default -> source.skip(chunk.size());
            }

            if (source.position() != end) {
                throw new IOException("Chunk not fully read");
            }
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
