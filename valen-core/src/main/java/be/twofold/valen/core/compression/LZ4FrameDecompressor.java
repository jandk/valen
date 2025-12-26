package be.twofold.valen.core.compression;

import be.twofold.valen.core.hashing.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.util.*;

final class LZ4FrameDecompressor implements Decompressor {
    private static final HashFunction HASH = HashFunction.xxHash32(0);
    static final LZ4FrameDecompressor INSTANCE = new LZ4FrameDecompressor();

    private LZ4FrameDecompressor() {
    }

    @Override
    public void decompress(Bytes src, Bytes.Mutable dst) throws IOException {
        try (var reader = BinarySource.wrap(src)) {
            decompress(reader, dst);
        }
    }

    private void decompress(BinarySource source, Bytes.Mutable dst) throws IOException {
        var frameHeader = Lz4FrameHeader.read(source);

        int dstOffset = 0;
        while (source.position() < source.size()) {
            var blockHeader = Lz4BlockHeader.read(source, frameHeader.blockMaximumSize());
            if (blockHeader.blockSize() == 0 && !blockHeader.uncompressed()) {
                break;
            }
            var blockData = source.readBytes(blockHeader.blockSize());
            if (frameHeader.flags().contains(Lz4FrameFlag.BLOCK_CHECKSUM)) {
                var blockChecksum = source.readInt();
                if (HASH.hash(blockData).asInt() != blockChecksum) {
                    throw new IOException("Invalid block checksum");
                }
            }
            if (blockHeader.uncompressed()) {
                blockData.copyTo(dst, dstOffset);
                dstOffset += blockData.length();
            } else {
                dstOffset += LZ4BlockDecompressor.INSTANCE.decompress(blockData, dst, dstOffset);
            }
        }
        if (dstOffset != dst.length()) {
            throw new IOException("Error while decompressing: expected " + dst.length() + " bytes, but only have " + dstOffset);
        }
        if (frameHeader.flags().contains(Lz4FrameFlag.CONTENT_CHECKSUM)) {
            int contentChecksum = source.readInt();
            if (HASH.hash(dst).asInt() != contentChecksum) {
                throw new IOException("Invalid content checksum");
            }
        }
    }

    private record Lz4FrameHeader(
        Set<Lz4FrameFlag> flags,
        int blockMaximumSize,
        OptionalLong contentSize,
        OptionalInt dictionaryId,
        byte headerChecksum
    ) {
        public static Lz4FrameHeader read(BinarySource source) throws IOException {
            source.expectInt(0x184D2204); // magic

            var flg = source.readByte();
            if ((flg & 0xC2) != 0x40) {
                throw new IOException("Invalid FLG byte");
            }
            var flags = Lz4FrameFlag.fromValue(flg & 0x3D);

            var bd = source.readByte();
            if ((bd & 0x8F) != 0x00) {
                throw new IOException("Invalid BD byte");
            }
            var blockMaximumSize = switch ((bd >>> 4) & 0x07) {
                case 4 -> 1024 * 64;
                case 5 -> 1024 * 256;
                case 6 -> 1024 * 1024;
                case 7 -> 1024 * 1024 * 4;
                default -> throw new IOException("Invalid Block Maximum Size");
            };

            var contentSize = flags.contains(Lz4FrameFlag.CONTENT_SIZE)
                    ? OptionalLong.of(source.readLong())
                : OptionalLong.empty();
            var dictionaryId = flags.contains(Lz4FrameFlag.DICTIONARY_ID)
                    ? OptionalInt.of(source.readInt())
                : OptionalInt.empty();

            var header = Bytes.Mutable
                .allocate(2 + (contentSize.isPresent() ? 8 : 0) + (dictionaryId.isPresent() ? 4 : 0))
                .set(0, flg)
                .set(1, bd);
            contentSize.ifPresent(l -> header.setLong(2, l));
            dictionaryId.ifPresent(i -> header.setInt(2 + (contentSize.isPresent() ? 8 : 0), i));
            var checkSum = (byte) (HASH.hash(header).asInt() >> 8);

            // TODO: Validate this
            var headerChecksum = source.readByte();
            if (checkSum != headerChecksum) {
                throw new IOException("Invalid checksum");
            }

            return new Lz4FrameHeader(
                flags,
                blockMaximumSize,
                contentSize,
                dictionaryId,
                headerChecksum
            );
        }
    }

    private enum Lz4FrameFlag implements FlagEnum {
        DICTIONARY_ID(1 << 0),
        CONTENT_CHECKSUM(1 << 2),
        CONTENT_SIZE(1 << 3),
        BLOCK_CHECKSUM(1 << 4),
        BLOCK_INDEPENDENCE(1 << 5),
        ;

        private final int value;

        Lz4FrameFlag(int value) {
            this.value = value;
        }

        public static Set<Lz4FrameFlag> fromValue(int value) {
            return FlagEnum.fromValue(Lz4FrameFlag.class, value);
        }

        @Override
        public int value() {
            return value;
        }
    }

    private record Lz4BlockHeader(
        int blockSize,
        boolean uncompressed
    ) {
        public static Lz4BlockHeader read(BinarySource source, int blockMaximumSize) throws IOException {
            var value = source.readInt();
            var blockSize = value & 0x7FFF_FFFF;
            var uncompressed = value < 0;
            if (!uncompressed && blockSize > blockMaximumSize) {
                throw new IOException("Compressed block size bigger than block maximum size");
            }
            return new Lz4BlockHeader(blockSize, uncompressed);
        }
    }
}
