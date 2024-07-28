package be.twofold.valen.reader.image;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.manager.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;
import dagger.*;
import jakarta.inject.*;

import java.io.*;

public final class ImageReader implements ResourceReader<Texture> {
    private final Lazy<FileManager> fileManager;
    private final boolean readStreams;

    @Inject
    ImageReader(Lazy<FileManager> fileManager) {
        this(fileManager, true);
    }

    ImageReader(Lazy<FileManager> fileManager, boolean readStreams) {
        this.fileManager = fileManager;
        this.readStreams = readStreams;
    }

    @Override
    public boolean canRead(Resource entry) {
        return entry.type() == ResourceType.Image;
    }

    @Override
    public Texture read(DataSource source, Resource resource) throws IOException {
        Image image = read(source, resource.hash());
        return new ImageMapper().map(image);
    }

    public Image read(DataSource source, long hash) throws IOException {
        var image = Image.read(source);
        source.expectEnd();

        if (readStreams) {
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
        }

        return image;
    }

    private void readSingleStream(Image image, long hash) throws IOException {
        var lastMip = image.mipInfos().getLast();
        var uncompressedSize = lastMip.cumulativeSizeStreamDB() + lastMip.decompressedSize();
        var buffer = fileManager.get().readStream(hash, uncompressedSize);
        var mipSource = ByteArrayDataSource.fromBuffer(buffer);
        for (var i = 0; i < image.header().totalMipCount(); i++) {
            image.mipData()[i] = mipSource.readBytes(image.mipInfos().get(i).decompressedSize());
        }
    }

    private void readMultiStream(Image image, long hash) throws IOException {
        for (var i = 0; i < image.header().startMip(); i++) {
            var mip = image.mipInfos().get(i);
            var mipHash = hash << 4 | (image.header().mipCount() - mip.mipLevel());
            if (fileManager.get().containsStream(mipHash)) {
                var mipBuffer = fileManager.get().readStream(mipHash, mip.decompressedSize());
                image.mipData()[i] = Buffers.toArray(mipBuffer);
            }
        }
    }
}
