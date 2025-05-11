package be.twofold.valen.game.greatcircle.reader.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.io.*;

public final class ImageReader implements AssetReader<Texture, GreatCircleAsset> {
    private final GreatCircleArchive archive;
    private final boolean readStreams;

    public ImageReader(GreatCircleArchive archive) {
        this(archive, true);
    }

    ImageReader(GreatCircleArchive archive, boolean readStreams) {
        this.archive = archive;
        this.readStreams = readStreams;
    }

    @Override
    public boolean canRead(GreatCircleAsset asset) {
        return asset.id().type() == ResourceType.image
            && !asset.id().name().filename().endsWith(".bimage");
    }

    @Override
    public Texture read(DataSource source, GreatCircleAsset asset) throws IOException {
        var image = Image.read(source);
        source.expectEnd();

        if (readStreams) {
            /*
             * Not entirely sure, but it seems to work, so I'm calling it the "single stream" format
             * Specified by a boolean at offset 0x38 in the header. Could mean something else though...
             * What is also strange is that the "single stream" format is used only for light probes.
             */
            if (image.header().singleStream()) {
                throw new UnsupportedOperationException("Single stream not supported");
                // readSingleStream(image, hash);
            } else {
                readMultiStream(image);
            }
        }

        return new ImageMapper().map(image);
    }

    private void readSingleStream(Image image, long hash) throws IOException {
//        var lastMip = image.sliceInfos().getLast();
//        var uncompressedSize = lastMip.cumulativeSizeStreamDB() + lastMip.decompressedSize();
//        var bytes = archive.readStream(hash, uncompressedSize);
//        try (var mipSource = DataSource.fromArray(bytes)) {
//            for (var i = 0; i < image.header().totalMipCount(); i++) {
//                image.slices()[i] = mipSource.readBytes(image.sliceInfos().get(i).decompressedSize());
//            }
//        }
    }

    private void readMultiStream(Image image) throws IOException {
        for (var i = 0; i < image.header().startMip(); i++) {
            var mip = image.sliceInfos().get(i);

            if (archive.containsStream(mip.hash())) {
                image.slices()[i] = archive.readStream(mip.hash(), mip.decompressedSize());
            }
        }
    }
}
