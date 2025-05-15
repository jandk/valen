package be.twofold.valen.game.darkages.reader.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.Hash;
import be.twofold.valen.game.darkages.reader.resources.*;

import java.io.*;
import java.nio.*;

public final class ImageReader implements AssetReader<Texture, DarkAgesAsset> {
    private final DarkAgesArchive archive;
    private final boolean readStreams;

    public ImageReader(DarkAgesArchive archive) {
        this(archive, true);
    }

    ImageReader(DarkAgesArchive archive, boolean readStreams) {
        this.archive = archive;
        this.readStreams = readStreams;
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.Image;
    }

    @Override
    public Texture read(DataSource source, DarkAgesAsset asset) throws IOException {
        var image = read(source, asset.hash());
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
        var bytes = archive.readStream(hash, uncompressedSize);
        try (var mipSource = DataSource.fromBuffer(bytes)) {
            for (var i = 0; i < image.header().totalMipCount(); i++) {
                image.mipData()[i] = mipSource.readBuffer(image.mipInfos().get(i).decompressedSize());
            }
        }
    }

    private void readMultiStream(Image image, long hash) throws IOException {
        ByteBuffer key = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        key.putLong(0, hash);
        for (var i = 0; i < image.header().startMip(); i++) {
            var mip = image.mipInfos().get(i);
            key.putInt(8, image.header().streamDBMipCount() - mip.mipLevel() - 1);
            var mipHash = Hash.hash(key);
            if (archive.containsStream(mipHash)) {
                image.mipData()[i] = archive.readStream(mipHash, mip.decompressedSize());
            }
        }
    }
}
