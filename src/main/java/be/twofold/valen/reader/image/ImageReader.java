package be.twofold.valen.reader.image;

import be.twofold.valen.*;
import be.twofold.valen.reader.resource.*;

import java.nio.*;
import java.util.*;

public final class ImageReader {
    private final ByteBuffer buffer;
    private final StreamLoader loader;
    private final ResourcesEntry entry;

    private ImageHeader header;
    private List<ImageMip> mips;
    private byte[][] mipData;

    public ImageReader(ByteBuffer buffer, StreamLoader loader, ResourcesEntry entry) {
        this.buffer = buffer;
        this.loader = loader;
        this.entry = entry;
    }

    public Image read(boolean readMips) {
        header = IOUtils.readStruct(buffer, ImageHeader.Size, ImageHeader::read);
        mips = IOUtils.readStructs(buffer, header.totalMipCount(), ImageMip.Size, ImageMip::read);
        if (readMips) {
            mipData = readEmbeddedMips();
            readStreamedMips();
        }

        // Sanity check, if this fails, we have a misunderstanding of the format
        if (buffer.hasRemaining()) {
            throw new UnsupportedOperationException("Buffer has remaining bytes");
        }
        return new Image(header, mips, mipData);
    }

    private byte[][] readEmbeddedMips() {
        byte[][] mipData = new byte[mips.size()][];
        for (int i = header.startMip(); i < mips.size(); i++) {
            mipData[i] = IOUtils.readBytes(buffer, mips.get(i).decompressedSize());
        }
        return mipData;
    }

    private void readStreamedMips() {
        if (header.unkBool1()) {
            // I think this means there's only one big ass stream entry
            if (!loader.exists(entry.streamResourceHash())) {
                throw new IllegalStateException("Could not find big entry");
            }
            return;
        }

        int minMip = Integer.parseInt(entry.name().properties().getOrDefault("minmip", "0"));
        for (int i = minMip; i < header.startMip(); i++) {
            ImageMip mip = mips.get(i);
            int index = header.mipCount() - mip.mipLevel();
            long hash = entry.streamResourceHash() << 4 | index;
            mipData[i] = loader.load(hash, mip.decompressedSize()).orElse(null);
        }
    }
}
