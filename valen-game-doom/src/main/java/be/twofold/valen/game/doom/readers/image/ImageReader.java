package be.twofold.valen.game.doom.readers.image;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.doom.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public final class ImageReader implements AssetReader.Binary<Texture, DoomAsset> {
    @Override
    public boolean canRead(DoomAsset asset) {
        return asset.rawType().equals("image");
    }

    @Override
    public Texture read(BinarySource source, DoomAsset asset, LoadingContext context) throws IOException {
        var image = Image.read(source);

        var format = mapFormat(image.header().format());
        var surfaces = new Surface[image.mips().size()];
        var numFaces = image.header().type() == 2 ? 6 : 1;
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

    private TextureFormat mapFormat(int format) {
        return switch (format) {
            case 2 -> TextureFormat.R16G16B16A16_SFLOAT;
            case 3 -> TextureFormat.R8G8B8A8_UNORM;
            case 5 -> TextureFormat.R8_UNORM;
            case 6 -> TextureFormat.R8G8_UNORM; // L8A8
            case 10 -> TextureFormat.BC1_UNORM;
            case 11 -> TextureFormat.BC3_UNORM;
            case 23 -> TextureFormat.BC7_UNORM;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
