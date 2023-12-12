package be.twofold.valen.reader.image;

import be.twofold.valen.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.resource.*;

import java.util.*;

public final class ImageReader {
    private final BetterBuffer buffer;
    private final FileManager fileManager;
    private final Resource resource;

    private ImageHeader header;
    private List<ImageMipInfo> mipInfos;
    private byte[][] mipData;

    public ImageReader(BetterBuffer buffer, FileManager fileManager, Resource resource) {
        this.buffer = buffer;
        this.fileManager = fileManager;
        this.resource = resource;
    }

    public Image read(boolean readMips) {
        header = ImageHeader.read(buffer);
        mipInfos = buffer.getStructs(header.totalMipCount(), ImageMipInfo::read);
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
        BetterBuffer buffer = fileManager.readStream(resource.hash(), uncompressedSize);
        readMipsFromBuffer(buffer, 0);
    }

    private void loadMultiStream() {
        int minMip = Integer.parseInt(resource.name().properties().getOrDefault("minmip", "0"));
        for (int i = minMip; i < header.startMip(); i++) {
            ImageMipInfo mip = mipInfos.get(i);
            long hash = resource.hash() << 4 | (header.mipCount() - mip.mipLevel());
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

    private byte[] readMip(BetterBuffer buffer, ImageMipInfo mip) {
        return buffer.getBytes(mip.decompressedSize());
    }
}
