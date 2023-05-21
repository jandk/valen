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
        mipData = new byte[mips.size()][];
        readMipsFromBuffer(buffer, header.startMip());
        if (readMips) {
            // Not entirely sure, but it seems to work, so I'm calling it the "single stream" format
            // Specified by a boolean at offset 0x38 in the header. Could mean something else though...
            // What is also strange is that the "single stream" format is used only for light probes.
            // And it seems to error the oodle decompression a lot of times as well, even though the
            // data is correct. So... yeah, I'm not sure what's going on here.
            if (header.unkBool1()) {
                loadSingleStream();
            } else {
                loadMultiStream();
            }
        }

        // Sanity check, if this fails, we have a misunderstanding of the format
        assert !buffer.hasRemaining() : "Buffer has remaining bytes";
        return new Image(header, mips, mipData);
    }

    private void loadSingleStream() {
        int uncompressedSize = mips.get(mips.size() - 1).cumulativeSizeStreamDB();
        byte[] uncompressed = loader
            .load(entry.streamResourceHash(), uncompressedSize)
            .orElseThrow(() -> new IllegalStateException("Could not load single stream image"));

        readMipsFromBuffer(ByteBuffer.wrap(uncompressed), 0);
    }

    private void loadMultiStream() {
        int minMip = Integer.parseInt(entry.name().properties().getOrDefault("minmip", "0"));
        for (int i = minMip; i < header.startMip(); i++) {
            ImageMip mip = mips.get(i);
            int mipIndex = header.mipCount() - mip.mipLevel();
            long hash = entry.streamResourceHash() << 4 | mipIndex;
            Optional<byte[]> loaded = loader.load(hash, mip.decompressedSize());
            if (loaded.isEmpty()) {
                System.err.println("Could not load mip " + mip.mipLevel() + " of " + entry.name());
            }
            mipData[i] = loaded.orElse(null);
        }
    }

    private void readMipsFromBuffer(ByteBuffer bytes, int start) {
        for (int i = start; i < mips.size(); i++) {
            mipData[i] = IOUtils.readBytes(bytes, mips.get(i).decompressedSize());
        }
    }
}
