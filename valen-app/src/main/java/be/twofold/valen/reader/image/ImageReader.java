package be.twofold.valen.reader.image;

import be.twofold.valen.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public final class ImageReader {
    private final FileManager fileManager;

    public ImageReader(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Image read(BetterBuffer buffer, boolean readStreams, long hash) {
        var header = ImageHeader.read(buffer);
        var mipInfos = buffer.getStructs(header.totalMipCount(), ImageMipInfo::read);
        var mipData = new byte[mipInfos.size()][];

        for (int i = header.startMip(); i < header.totalMipCount(); i++) {
            mipData[i] = readMip(buffer, mipInfos.get(i));
        }
        buffer.expectEnd();

        if (readStreams) {
            // Not entirely sure, but it seems to work, so I'm calling it the "single stream" format
            // Specified by a boolean at offset 0x38 in the header. Could mean something else though...
            // What is also strange is that the "single stream" format is used only for light probes.
            if (header.singleStream()) {
                readSingleStream(header, mipInfos, mipData, hash);
            } else {
                readMultiStream(header, mipInfos, mipData, hash);
            }
        }

        return new Image(header, mipInfos, mipData);
    }

    private void readSingleStream(ImageHeader header, List<ImageMipInfo> mipInfos, byte[][] mipData, long hash) {
        ImageMipInfo lastMip = mipInfos.getLast();
        var uncompressedSize = lastMip.cumulativeSizeStreamDB() + lastMip.decompressedSize();
        var mipBuffer = fileManager.readStream(hash, uncompressedSize);
        for (int i = 0; i < header.mipCount(); i++) {
            mipData[i] = readMip(mipBuffer, mipInfos.get(i));
        }
    }

    private void readMultiStream(ImageHeader header, List<ImageMipInfo> mipInfos, byte[][] mipData, long hash) {
        for (var i = 0; i < header.startMip(); i++) {
            var mip = mipInfos.get(i);
            var mipHash = hash << 4 | (header.mipCount() - mip.mipLevel());
            if (fileManager.streamExists(mipHash)) {
                var mipBuffer = fileManager.readStream(mipHash, mip.decompressedSize());
                mipData[i] = readMip(mipBuffer, mip);
            }
        }
    }

    private static byte[] readMip(BetterBuffer buffer, ImageMipInfo mipInfo) {
        return buffer.getBytes(mipInfo.decompressedSize());
    }
}
