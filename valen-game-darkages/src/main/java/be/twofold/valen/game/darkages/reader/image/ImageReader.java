package be.twofold.valen.game.darkages.reader.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class ImageReader implements AssetReader<Texture, DarkAgesAsset> {
    private final boolean readStreams;

    public ImageReader(boolean readStreams) {
        this.readStreams = readStreams;
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.Image;
    }

    @Override
    public Texture read(BinarySource source, DarkAgesAsset asset, LoadingContext context) throws IOException {
        var image = read(source, asset.hash(), context);
        return new ImageMapper().map(image);
    }

    public Image read(BinarySource source, long hash, LoadingContext context) throws IOException {
        var image = Image.read(source);
        source.expectEnd();

        if (readStreams) {
            /*
             * Not entirely sure, but it seems to work, so I'm calling it the "single stream" format
             * Specified by a boolean at offset 0x38 in the header. Could mean something else though...
             * What is also strange is that the "single stream" format is used only for light probes.
             */
            if (image.header().singleStream()) {
                readSingleStream(image, hash, context);
            } else {
                readMultiStream(image, hash, context);
            }
        }

        return image;
    }

    private void readSingleStream(Image image, long hash, LoadingContext context) throws IOException {
        var lastMip = image.mipInfos().getLast();
        var uncompressedSize = lastMip.cumulativeSizeStreamDB() + lastMip.decompressedSize();
        long streamId = Hash.hash(hash, 0, 0);
        var bytes = context.open(new DarkAgesStreamLocation(streamId, uncompressedSize));
        try (var mipSource = BinarySource.wrap(bytes)) {
            for (var i = 0; i < image.header().totalMipCount(); i++) {
                image.mipData()[i] = mipSource.readBytes(image.mipInfos().get(i).decompressedSize());
            }
        }
    }

    private void readMultiStream(Image image, long hash, LoadingContext context) throws IOException {
        for (var i = 0; i < image.header().startMip(); i++) {
            var mip = image.mipInfos().get(i);
            int mipStreamId = image.header().streamDBMipCount() - mip.mipLevel() - 1;
            var streamId = Hash.hash(hash, mipStreamId, 0);
            // TODO: Handle missing streams
            //if (context.containsStream(streamId)) {
            image.mipData()[i] = context.open(new DarkAgesStreamLocation(streamId, mip.decompressedSize()));
            //}
        }
    }
}
