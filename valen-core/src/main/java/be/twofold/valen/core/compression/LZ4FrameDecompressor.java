package be.twofold.valen.core.compression;

import be.twofold.valen.core.hashing.*;
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
    public void decompress(Bytes src, MutableBytes dst) throws IOException {
        int srcOffset = 0;

        var frameHeader = Lz4FrameHeader.read(src);
        srcOffset += frameHeader.size();

        int dstOffset = 0;
        while (srcOffset < src.length()) {
            var blockHeader = Lz4BlockHeader.read(src.slice(srcOffset), frameHeader.blockMaximumSize());
            srcOffset += 4;

            if (blockHeader.blockSize() == 0 && !blockHeader.uncompressed()) {
                break;
            }
            var blockData = src.slice(srcOffset, blockHeader.blockSize());
            srcOffset += blockHeader.blockSize();

            if (frameHeader.flags().contains(Lz4FrameFlag.BLOCK_CHECKSUM)) {
                var blockChecksum = src.getInt(srcOffset);
                if (HASH.hash(blockData).asInt() != blockChecksum) {
                    throw new IOException("Invalid block checksum");
                }
                srcOffset += 4;
            }
            if (blockHeader.uncompressed()) {
                blockData.copyTo(dst, dstOffset);
                dstOffset += blockData.length();
            } else {
                dstOffset += LZ4Decompressor.INSTANCE.decompress(blockData, dst, dstOffset);
            }
        }
        if (dstOffset != dst.length()) {
            throw new IOException("Error while decompressing: expected " + dst.length() + " bytes, but only have " + dstOffset);
        }
        if (frameHeader.flags().contains(Lz4FrameFlag.CONTENT_CHECKSUM)) {
            int contentChecksum = src.getInt(srcOffset);
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
        public static Lz4FrameHeader read(Bytes reader) throws IOException {
            if (reader.getInt(0) != 0x184D2204) {
                throw new IOException("Invalid magic");
            }

            var flg = reader.get(4);
            if ((flg & 0xC2) != 0x40) {
                throw new IOException("Invalid FLG byte");
            }
            var flags = Lz4FrameFlag.fromValue(flg & 0x3D);

            var bd = reader.get(5);
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

            int offset = 6;
            OptionalLong contentSize;
            if (flags.contains(Lz4FrameFlag.CONTENT_SIZE)) {
                contentSize = OptionalLong.of(reader.getLong(offset));
                offset += 8;
            } else {
                contentSize = OptionalLong.empty();
            }

            OptionalInt dictionaryId;
            if (flags.contains(Lz4FrameFlag.DICTIONARY_ID)) {
                dictionaryId = OptionalInt.of(reader.getInt(offset));
                offset += 4;
            } else {
                dictionaryId = OptionalInt.empty();
            }

            var header = MutableBytes
                .allocate(offset - 4)
                .set(0, flg)
                .set(1, bd);
            contentSize.ifPresent(l -> header.setLong(2, l));
            dictionaryId.ifPresent(i -> header.setInt(2 + (contentSize.isPresent() ? 8 : 0), i));
            var checkSum = (byte) (HASH.hash(header).asInt() >> 8);

            // TODO: Validate this
            var headerChecksum = reader.get(offset);
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

        int size() {
            return 7 + (contentSize.isPresent() ? 8 : 0) + (dictionaryId.isPresent() ? 4 : 0);
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
        public static Lz4BlockHeader read(Bytes reader, int blockMaximumSize) throws IOException {
            var value = reader.getInt(0);
            var blockSize = value & 0x7FFF_FFFF;
            var uncompressed = value < 0;
            if (!uncompressed && blockSize > blockMaximumSize) {
                throw new IOException("Compressed block size bigger than block maximum size");
            }
            return new Lz4BlockHeader(blockSize, uncompressed);
        }
    }
}
