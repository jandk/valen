package be.twofold.valen.game.source.readers.vtf;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.source.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.invoke.*;
import java.util.*;
import java.util.function.*;

public final class VtfReader implements AssetReader.Binary<Texture, SourceAsset> {
    @Override
    public boolean canRead(SourceAsset asset) {
        return asset.id().extension().equals("vtf");
    }

    @Override
    public Texture read(BinarySource source, SourceAsset asset, LoadingContext context) throws IOException {
        var vtf = Vtf.read(source);
        var header = vtf.header();

        var highRes = vtf.resources().stream()
            .filter(r -> r.tag() == VtfResourceTag.HIGH_RES)
            .findFirst();

        source.position(header.headerSize());
        if (highRes.isPresent()) {
            source.position(highRes.get().offset());
        } else if (header.lowResImageFormat() != VtfImageFormat.UNKNOWN) {
            var lowResSize = header.lowResImageFormat()
                .toTextureFormat(false)
                .surfaceSize(header.lowResImageWidth(), header.lowResImageHeight(), 1);
            source.skip(lowResSize);
        }

        boolean srgb = header.flags().contains(VtfFlags.TEXTUREFLAGS_SRGB);
        var format = header.highResImageFormat().toTextureFormat(srgb);
        boolean cubeMap = header.flags().contains(VtfFlags.TEXTUREFLAGS_ENVMAP);

        int mips = header.mipmapCount();
        int frames = header.frames();
        int faces = cubeMap ? 7 : 1;

        var surfaces = new Surface[mips * frames * Math.min(faces, 6)];
        for (int mip = header.mipmapCount() - 1; mip >= 0; mip--) {
            int mipWidth = Math.max(header.width() >> mip, 1);
            int mipHeight = Math.max(header.height() >> mip, 1);
            int mipDepth = Math.max(header.depth() >> mip, 1);
            int mipSize = format.surfaceSize(mipWidth, mipHeight, mipDepth);

            for (int frame = 0; frame < frames; frame++) {
                for (int face = 0; face < faces; face++) {
                    if (face == 6) {
                        source.skip(mipSize);
                        continue;
                    }
                    int index = face * mips + mip;
                    surfaces[index] = new Surface(format, mipWidth, mipHeight, mipDepth, source.readBytes(mipSize));
                }
            }
        }

        return new Texture(
            format,
            cubeMap ? TextureKind.CUBE_MAP : TextureKind.TEXTURE_2D,
            header.width(),
            header.height(),
            cubeMap ? 6 : 1,
            List.of(surfaces),
            UnaryOperator.identity()
        );
    }

    @Override
    public Optional<Meta.Node> readMetadata(SourceAsset asset, LoadingContext context) throws IOException {
        try (var source = BinarySource.wrap(context.open(asset.location()))) {
            var vtf = Vtf.read(source);
            return Optional.of(Meta.build(MethodHandles.lookup(), vtf));
        }
    }
}
