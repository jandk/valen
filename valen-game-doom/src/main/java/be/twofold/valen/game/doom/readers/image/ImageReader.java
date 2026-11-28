package be.twofold.valen.game.doom.readers.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.reader.image.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public final class ImageReader implements AssetReader.Binary<Texture, DoomAsset> {
    private static final Set<ResourceType> TYPES = Set.of(
        ResourceType.of("decalatlas"),
        ResourceType.of("image"),
        ResourceType.of("lightatlas"),
        ResourceType.of("transsortatlas")
    );

    @Override
    public boolean canRead(DoomAsset asset) {
        return TYPES.contains(asset.id().type())
            || asset instanceof DoomAsset.Resource resource && resource.isBImage();
    }

    @Override
    public Texture read(BinaryReader reader, DoomAsset asset, LoadingContext context) throws IOException {
        var image = Image.read(reader);

        var format = image.header().format().toImageFormat();
        var surfaces = new Surface[image.mips().size()];
        var numFaces = image.header().type() == TextureType.TT_CUBIC ? 6 : 1;
        for (int mip = 0, i = 0; mip < image.header().mipCount(); mip++) {
            for (int face = 0; face < numFaces; face++, i++) {
                var current = image.mips().get(i);
                surfaces[face * image.header().mipCount() + mip] = new Surface(format, current.width(), current.height(), 1, current.data());
            }
        }

        return new Texture(
            format,
            numFaces == 1 ? TextureKind.TEXTURE_2D : TextureKind.CUBE_MAP,
            image.header().width(),
            image.header().height(),
            numFaces,
            List.of(surfaces),
            UnaryOperator.identity()
        );
    }
}
