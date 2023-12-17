package be.twofold.valen.reader.image;

import be.twofold.valen.core.util.*;
import be.twofold.valen.manager.*;

public final class ImageReader {
    private final FileManager fileManager;

    public ImageReader(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public Image read(BetterBuffer buffer, boolean readStreams, long hash) {
        var image = Image.read(buffer);

        if (readStreams) {
            // Not entirely sure, but it seems to work, so I'm calling it the "single stream" format
            // Specified by a boolean at offset 0x38 in the header. Could mean something else though...
            // What is also strange is that the "single stream" format is used only for light probes.
            if (image.header().singleStream()) {
                readSingleStream(image, hash);
            } else {
                readMultiStream(image, hash);
            }
        }

        return image;
    }

    private void readSingleStream(Image image, long hash) {
        var lastMip = image.mipInfos().getLast();
        var uncompressedSize = lastMip.cumulativeSizeStreamDB() + lastMip.decompressedSize();
        var mipBuffer = fileManager.readStream(hash, uncompressedSize);
        for (var i = 0; i < image.header().mipCount(); i++) {
            image.mipData()[i] = mipBuffer.getBytes(image.mipInfos().get(i).decompressedSize());
        }
    }

    private void readMultiStream(Image image, long hash) {
        for (var i = 0; i < image.header().startMip(); i++) {
            var mip = image.mipInfos().get(i);
            var mipHash = hash << 4 | (image.header().mipCount() - mip.mipLevel());
            if (fileManager.streamExists(mipHash)) {
                var mipBuffer = fileManager.readStream(mipHash, mip.decompressedSize());
                image.mipData()[i] = mipBuffer.getBytes(mip.decompressedSize());
            }
        }
    }

}
