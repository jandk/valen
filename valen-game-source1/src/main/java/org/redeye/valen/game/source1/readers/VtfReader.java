package org.redeye.valen.game.source1.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import org.redeye.valen.game.source1.*;
import org.redeye.valen.game.source1.vtf.*;

import java.io.*;
import java.util.*;


public class VtfReader implements Reader<Texture> {

    public static final byte[] HiResKey = {0x30, 0, 0};

    public Texture read(Archive archive, Asset asset, DataSource source) throws IOException {
        var ident = source.readString(3);
        if (!ident.equals("VTF")) {
            return null;
        }
        source.skip(1);
        final int versionMj = source.readInt();
        final int versionMn = source.readInt();
        final int headerSize = source.readInt();
        final short width = source.readShort();
        final short height = source.readShort();
        final int flags = source.readInt();
        final short frames = source.readShort();
        final short firstFrame = source.readShort();
        source.skip(4); // padding
        source.skip(16); // reflectivity
        source.skip(4); // float Bump scale
        final PixelFormat hiResFormat = PixelFormat.values()[source.readInt() + 1];
        final byte mipCount = source.readByte();
        final PixelFormat loResFormat = PixelFormat.values()[source.readInt() + 1];
        final byte loWidth = source.readByte();
        final byte loHeight = source.readByte();
        final short depth = versionMn >= 2 ? source.readShort() : 0;
        source.skip(3);
        final int resourceCount;
        if (versionMn >= 3) {
            resourceCount = source.readInt();
            source.skip(8);
        } else {
            resourceCount = 0;
        }

        final List<ResourceEntry> resources = new ArrayList<>();
        for (int i = 0; i < resourceCount; i++) {
            resources.add(new ResourceEntry(source.readBytes(3), source.readByte(), source.readInt()));
        }
        final TextureFormat textureFormat = hiResFormat.toTextureFormat();
        if (textureFormat == null) {
            return null;
        }
        final List<Surface> surfaces = new ArrayList<>();
        if (resourceCount == 0) {
            source.seek(headerSize);
            source.skip((long) (loWidth / 4) * (loHeight / 4) * loResFormat.blockSize());
        } else {
            var resource = resources.stream().filter(e -> Arrays.equals(e.tag(), HiResKey)).findFirst().orElseThrow();
            source.seek(resource.offset());
        }

        final int minRes = hiResFormat.isBlockCompressed() ? 4 : 1;
        for (int mipId = (mipCount - 1); mipId >= 0; mipId--) {
            final int mipWidth = Math.max(width >> (mipId), minRes);
            final int mipHeight = Math.max(height >> (mipId), minRes);
            final Surface surface = new Surface(mipWidth, mipHeight, hiResFormat.toTextureFormat(), source.readBytes((mipHeight / minRes) * (mipWidth / minRes) * hiResFormat.blockSize()));
            surfaces.add(surface);
        }

        return new Texture(width, height, textureFormat, surfaces.reversed(), false);
    }

    @Override
    public boolean canRead(Asset asset) {
        if (asset.id() instanceof SourceAssetID sourceId) {
            return sourceId.extension().equals("vtf");
        }
        return false;
    }


}
