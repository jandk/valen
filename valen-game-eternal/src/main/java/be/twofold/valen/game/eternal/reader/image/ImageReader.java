package be.twofold.valen.game.eternal.reader.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.stream.*;

public final class ImageReader implements AssetReader<Texture, EternalAsset> {
    private final EternalArchive archive;
    private final boolean readStreams;

    public ImageReader(EternalArchive archive) {
        this(archive, true);
    }

    ImageReader(EternalArchive archive, boolean readStreams) {
        this.archive = archive;
        this.readStreams = readStreams;
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.Image;
    }

    @Override
    public Texture read(BinarySource source, EternalAsset resource) throws IOException {
        long hash = resource.hash();
        var image = Image.read(source);

        Bytes[] mipData = new Bytes[totalMipCount(image.header())];
        for (int i = startMip(image.header()); i < totalMipCount(image.header()); i++) {
            mipData[i] = source.readBytes(image.mipInfos().get(i).decompressedSize());
        }
        source.expectEnd();

        if (readStreams) {
            /*
             * Not entirely sure, but it seems to work, so I'm calling it the "single stream" format
             * Specified by a boolean at offset 0x38 in the header. Could mean something else though...
             * What is also strange is that the "single stream" format is used only for light probes.
             */
            if (image.header().singleStream()) {
                readSingleStream(image, hash, mipData);
            } else {
                readMultiStream(image, hash, mipData);
            }
        }

        return new ImageMapper().map(image, mipData, minMip(mipData));
    }

    private void readSingleStream(Image image, long hash, Bytes[] mipData) throws IOException {
        var lastMip = image.mipInfos().getLast();
        var uncompressedSize = lastMip.cumulativeSizeStreamDb() + lastMip.decompressedSize();
        var bytes = archive.readStream(hash, uncompressedSize);
        try (var mipSource = BinarySource.wrap(bytes)) {
            for (var i = 0; i < totalMipCount(image.header()); i++) {
                mipData[i] = mipSource.readBytes(image.mipInfos().get(i).decompressedSize());
            }
        }
    }

    private void readMultiStream(Image image, long hash, Bytes[] mipData) throws IOException {
        for (var i = 0; i < startMip(image.header()); i++) {
            var mip = image.mipInfos().get(i);
            var mipHash = hash << 4 | (image.header().mipCount() - mip.mipLevel());
            if (archive.containsStream(mipHash)) {
                mipData[i] = archive.readStream(mipHash, mip.decompressedSize());
            }
        }
    }

    // region Helpers

    private int minMip(Bytes[] mipData) {
        return IntStream.range(0, mipData.length)
            .filter(i -> mipData[i] != null)
            .findFirst()
            .orElse(-1);
    }

    private int startMip(ImageHeader header) {
        var mask = switch (header.textureType()) {
            case TT_2D -> 0x0f;
            case TT_CUBIC -> 0xff;
            default -> throw new UnsupportedOperationException("Unsupported texture type: " + header.textureType());
        };
        return header.streamDbMipCount() & mask;
    }

    private int totalMipCount(ImageHeader header) {
        var faces = switch (header.textureType()) {
            case TT_2D -> 1;
            case TT_CUBIC -> 6;
            default -> throw new UnsupportedOperationException("Unsupported texture type: " + header.textureType());
        };
        return header.mipCount() * faces;
    }

    // endregion

}
