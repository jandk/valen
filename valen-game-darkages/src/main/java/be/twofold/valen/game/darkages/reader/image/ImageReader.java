package be.twofold.valen.game.darkages.reader.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class ImageReader implements AssetReader<Texture, DarkAgesAsset> {
    private final BinaryStore<Long> streams;
    private final boolean readStreams;

    public ImageReader(BinaryStore<Long> streams) {
        this(streams, true);
    }

    ImageReader(BinaryStore<Long> streams, boolean readStreams) {
        this.streams = streams;
        this.readStreams = readStreams;
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.Image;
    }

    @Override
    public Texture read(BinarySource source, DarkAgesAsset asset) throws IOException {
        var image = read(source, asset.hash());
        return new ImageMapper().map(image);
    }

    public Image read(BinarySource source, long hash) throws IOException {
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
        var bytes = streams.read(Hash.hash(hash, 0, 0), OptionalInt.of(uncompressedSize));
        try (var mipSource = BinarySource.wrap(bytes)) {
            for (var i = 0; i < image.header().totalMipCount(); i++) {
                image.mipData()[i] = mipSource.readBytes(image.mipInfos().get(i).decompressedSize());
            }
        }
    }

    private void readMultiStream(Image image, long hash) throws IOException {
        for (var i = 0; i < image.header().startMip(); i++) {
            var mip = image.mipInfos().get(i);
            int streamID = image.header().streamDBMipCount() - mip.mipLevel() - 1;
            var mipHash = Hash.hash(hash, streamID, 0);
            if (streams.exists(mipHash)) {
                image.mipData()[i] = streams.read(mipHash, OptionalInt.of(mip.decompressedSize()));
            }
        }
    }
}
