package be.twofold.valen.reader.image;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import be.twofold.valen.stream.*;
import jakarta.inject.*;

import java.io.*;

public final class ImageReader implements ResourceReader<Texture> {
    private final StreamManager streamManager;

    @Inject
    public ImageReader(StreamManager streamManager) {
        this.streamManager = streamManager;
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.Image;
    }

    @Override
    public Texture read(DataSource source, Resource resource) throws IOException {
        Image image = read(source, true, resource.hash());
        return new ImageMapper().map(image);
    }

    public Image read(DataSource source, boolean readStreams, long hash) throws IOException {
        var image = Image.read(source);

        if (readStreams && streamManager != null) {
            /*
             * Not entirely sure, but it seems to work, so I'm calling it the "single stream" format
             * Specified by a boolean at offset 0x38 in the header. Could mean something else though...
             * What is also strange is that the "single stream" format is used only for light probes.
             */
            if (image.header().singleStream()) {
                readSingleStream(image, hash);
            } else {
                readMultiStream(image, hash);
            }
            // TODO: Implement
            // source.expectEnd();
        }

        return image;
    }

    private void readSingleStream(Image image, long hash) {
        var lastMip = image.mipInfos().getLast();
        var uncompressedSize = lastMip.cumulativeSizeStreamDB() + lastMip.decompressedSize();
        var mipBuffer = BetterBuffer.wrap(streamManager.read(hash, uncompressedSize));
        for (var i = 0; i < image.header().totalMipCount(); i++) {
            image.mipData()[i] = mipBuffer.getBytes(image.mipInfos().get(i).decompressedSize());
        }
    }

    private void readMultiStream(Image image, long hash) {
        for (var i = 0; i < image.header().startMip(); i++) {
            var mip = image.mipInfos().get(i);
            var mipHash = hash << 4 | (image.header().mipCount() - mip.mipLevel());
            if (streamManager.contains(mipHash)) {
                var mipBuffer = streamManager.read(mipHash, mip.decompressedSize());
                image.mipData()[i] = mipBuffer;
            }
        }
    }
}
