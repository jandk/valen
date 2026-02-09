package be.twofold.valen.game.source.readers.vtf;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.source.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class VtfReader implements AssetReader<Texture, SourceAsset> {
    @Override
    public boolean canRead(SourceAsset asset) {
        return asset.id().extension().equals("vtf");
    }

    @Override
    public Texture read(BinarySource source, SourceAsset asset, LoadingContext context) throws IOException {
        var header = VtfHeader.read(source);
        var resources = source.readObjects(header.numResources(), VtfResourceEntry::read);

        var surfaces = new ArrayList<Surface>();
        var highRes = resources.stream()
            .filter(r -> r.tag() == VtfResourceTag.HIGH_RES)
            .findFirst();

        if (highRes.isPresent()) {
            source.position(highRes.get().offset());
        } else {
            source.position(header.headerSize());
            var lowResSize = header.lowResImageFormat().blockSize()
                * (header.lowResImageWidth() / 4)
                * (header.lowResImageHeight() / 4);
            source.skip(lowResSize);
        }

        var format = header.highResImageFormat().toTextureFormat();
        for (int i = header.mipmapCount() - 1; i >= 0; i--) {
            int width = Math.max(header.width() >> i, 1);
            int height = Math.max(header.height() >> i, 1);
            int size = format.surfaceSize(width, height);
            surfaces.add(new Surface(width, height, format, source.readBytes(size).toArray()));
        }

        return new Texture(header.width(), header.height(), format, false, surfaces.reversed());
    }
}
