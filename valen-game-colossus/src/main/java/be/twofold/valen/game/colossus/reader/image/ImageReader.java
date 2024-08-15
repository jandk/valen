package be.twofold.valen.game.colossus.reader.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.colossus.*;
import be.twofold.valen.game.colossus.reader.*;
import be.twofold.valen.game.colossus.resource.*;

import java.io.*;

public final class ImageReader implements ResourceReader<Texture> {
    private final ColossusArchive archive;
    private final boolean readStreams;

    public ImageReader(ColossusArchive archive) {
        this(archive, true);
    }

    ImageReader(ColossusArchive archive, boolean readStreams) {
        this.archive = archive;
        this.readStreams = readStreams;
    }

    @Override
    public boolean canRead(ResourceKey key) {
        return key.type() == ResourceType.image;
    }

    @Override
    public Texture read(DataSource source, Asset asset) throws IOException {
        var image = Image.read(source);
        source.expectEnd();

        readMultiStream(image, (Long) asset.properties().get("hash"));

        return new ImageMapper().map(image);
    }

    private void readMultiStream(Image image, long hash) throws IOException {
        for (var i = 0; i < image.header().startMip(); i++) {
            var mip = image.mips().get(i);
            var mipHash = hash << 4 | (image.header().mipCount() - mip.mipLevel());
            if (archive.containsStream(mipHash)) {
                var buffer = archive.readStream(mipHash, mip.compressedSize(), mip.decompressedSize());
                image.mipData()[i] = Buffers.toArray(buffer);
            }
        }
    }
}
