package be.twofold.valen.game.qc.reader.pct;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.qc.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;
import java.util.function.*;

public final class PctReader implements AssetReader.Binary<Texture, QcAsset> {
    private static final int MAGIC = 0x50494354;

    @Override
    public boolean canRead(QcAsset asset) {
        return asset.id().extension().equals("pct");
    }

    @Override
    public Texture read(BinarySource source, QcAsset asset, LoadingContext context) throws IOException {
        Pct pct = Pct.read(source);

        int width = 0, height = 0, depth = 0, faceCount = 0;
        int format = 0, mipCount = 0;
        var mips = new ArrayList<Mip>();
        var pixels = (Bytes) null;

        for (PctTag tag : pct.tags()) {
            switch (tag.type()) {
                case MAGIC:
                    if (tag.data().getInt(0) != MAGIC) {
                        throw new IOException("Invalid PCT magic");
                    }
                    break;

                case DIMENSIONS:
                    width = tag.data().getInt(0);
                    height = tag.data().getInt(4);
                    depth = tag.data().getInt(8);
                    faceCount = tag.data().getInt(12);
                    break;

                case FORMAT:
                    format = tag.data().getInt(0);
                    break;

                case MIP_COUNT:
                    mipCount = tag.data().getInt(0);
                    break;

                case MIP_TABLE:
                    if (tag.data().length() != mipCount * 8) {
                        throw new IOException("Invalid mip table size");
                    }

                    for (int i = 0; i < mipCount; i++) {
                        mips.add(Mip.read(tag.data(), i * 8));
                    }
                    break;

                case PIXELS:
                    pixels = tag.data();
                    break;
            }
        }

        int faceSize = mips.stream()
            .mapToInt(Mip::size)
            .sum();

        var textureFormat = mapFormat(format);
        var surfaces = new ArrayList<Surface>();
        int offset = 0;
        for (int face = 0; face < faceCount; face++) {
            for (int mip = 0; mip < mipCount; mip++) {
                var data = pixels.slice(offset + mips.get(mip).offset(), mips.get(mip).size());
                int surfaceWidth = Math.max(1, width >> mip);
                int surfaceHeight = Math.max(1, height >> mip);
                int surfaceDepth = Math.max(1, depth >> mip);

                var surface = new Surface(textureFormat, surfaceWidth, surfaceHeight, surfaceDepth, data);
                surfaces.add(surface);
            }
            offset += faceSize;
        }

        TextureKind kind;
        if (faceCount == 6) {
            kind = TextureKind.CUBE_MAP;
            depth = 6;
        } else if (depth > 1) {
            kind = TextureKind.TEXTURE_3D;
        } else if (height == 1) {
            kind = TextureKind.TEXTURE_1D;
            depth = faceCount;
        } else {
            kind = TextureKind.TEXTURE_2D;
            depth = faceCount;
        }

        return new Texture(textureFormat, kind, width, height, depth, surfaces, UnaryOperator.identity());
    }

    private static TextureFormat mapFormat(int i) {
        return switch (i) {
            case 0, 22 -> TextureFormat.R8G8B8A8_UNORM;
            case 3 -> TextureFormat.R8_UNORM;
            case 12 -> TextureFormat.BC1_UNORM;
            case 17 -> TextureFormat.BC3_UNORM;
            case 36 -> TextureFormat.BC5_UNORM;
            case 37 -> TextureFormat.BC4_UNORM;
            case 38, 39 -> TextureFormat.R16G16B16A16_SFLOAT;
            case 49 -> TextureFormat.BC6H_UFLOAT;
            case 51, 52 -> TextureFormat.BC7_UNORM;
            default -> throw new UnsupportedOperationException("Unknown format: " + i);
        };
    }

    record Mip(int offset, int size) {
        static Mip read(Bytes source, int offset) throws IOException {
            return new Mip(source.getInt(offset), source.getInt(offset + 4));
        }
    }
}
