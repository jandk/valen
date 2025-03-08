package org.redeye.valen.game.spacemarines2.readers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;
import org.redeye.valen.game.spacemarines2.*;
import org.redeye.valen.game.spacemarines2.types.*;
import org.yaml.snakeyaml.*;

import java.io.*;
import java.util.*;

public class PCTReader implements Reader<Texture> {
    @Override
    public Texture read(Archive archive, Asset asset, DataSource source) throws IOException {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(source.readString((int) source.size()));

        Map<String, Object> header = (Map<String, Object>) data.get("header");
        List<String> mipMapLinks = (List<String>) data.get("mipMaps");
        int width = (int) header.get("sx");
        int height = (int) header.get("sy");
        int depth = (int) header.get("sz");
        if (depth > 1) {
            return null;
        }
        PictureFormat format = PictureFormat.values()[(int) header.get("format")];
        var surfaces = new ArrayList<Surface>();
        TextureFormat texFormat = format.mapToTextureFormat();
        for (int i = 0; i < mipMapLinks.size(); i++) {
            String mipMapLink = mipMapLinks.get(i);
            var rawMipData = archive.loadAsset(new EmperorAssetId("pct/" + mipMapLink), byte[].class);
            var minBlockSize = texFormat.block().height();
            var mipWidth = Math.max(width >> i, minBlockSize);
            var mipHeight = Math.max(height >> i, minBlockSize);
            Surface surface = new Surface(mipWidth, mipHeight, rawMipData);
            surfaces.add(surface);
        }
        return new Texture(width, height, texFormat, false, surfaces);
    }

    @Override
    public boolean canRead(AssetID id) {
        if (id instanceof EmperorAssetId emperorAssetId) {
            return emperorAssetId.inferAssetType() == AssetType.TEXTURE && emperorAssetId.fileName().endsWith(".pct.resource");
        }
        return false;
    }
}
