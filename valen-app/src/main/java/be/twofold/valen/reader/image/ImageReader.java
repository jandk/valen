package be.twofold.valen.reader.image;

import be.twofold.valen.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.resource.*;

import java.util.*;

public final class ImageReader {
    private final BetterBuffer buffer;
    private final FileManager fileManager;
    private final ResourcesEntry entry;

    private ImageHeader header;
    private List<ImageMipInfos> mipInfos;
    private byte[][] mipData;

    public ImageReader(BetterBuffer buffer, FileManager fileManager, ResourcesEntry entry) {
        this.buffer = buffer;
        this.fileManager = fileManager;
        this.entry = entry;
    }

    public Image read(boolean readMips) {
        header = ImageHeader.read(buffer);
        mipInfos = buffer.getStructs(header.totalMipCount(), ImageMipInfos::read);
        mipData = new byte[mipInfos.size()][];
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
        buffer.expectEnd();
        return new Image(header, mipInfos, mipData);
    }

    private void loadSingleStream() {
        int uncompressedSize = mipInfos.get(mipInfos.size() - 1).cumulativeSizeStreamDB();
        BetterBuffer buffer = fileManager.readStream(entry.defaultHash(), uncompressedSize);
        readMipsFromBuffer(buffer, 0);
    }

    private void loadMultiStream() {
        int minMip = Integer.parseInt(entry.name().properties().getOrDefault("minmip", "0"));
        for (int i = minMip; i < header.startMip(); i++) {
            ImageMipInfos mip = mipInfos.get(i);
            long hash = entry.defaultHash() << 4 | (header.mipCount() - mip.mipLevel());
            if (!fileManager.streamExists(hash)) {
                System.err.println("Stream not found: " + Long.toHexString(hash));
                continue;
            }
            BetterBuffer buffer = fileManager.readStream(hash, mip.decompressedSize());
            mipData[i] = readMip(buffer, mip);
        }
    }

    private void readMipsFromBuffer(BetterBuffer buffer, int start) {
        for (int i = start; i < mipInfos.size(); i++) {
            mipData[i] = readMip(buffer, mipInfos.get(i));
        }
    }

    private byte[] readMip(BetterBuffer buffer, ImageMipInfos mip) {
        return buffer.getBytes(mip.decompressedSize());
    }
}