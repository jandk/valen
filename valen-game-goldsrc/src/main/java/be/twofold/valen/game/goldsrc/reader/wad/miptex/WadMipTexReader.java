package be.twofold.valen.game.goldsrc.reader.wad.miptex;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.goldsrc.*;
import be.twofold.valen.game.goldsrc.reader.wad.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class WadMipTexReader implements AssetReader<Texture, GoldSrcAsset> {
    @Override
    public boolean canRead(GoldSrcAsset asset) {
        return asset instanceof GoldSrcAsset.Wad wadAsset
            && wadAsset.entry().type() == WadEntryType.MIPTEX;
    }

    @Override
    public Texture read(BinarySource source, GoldSrcAsset asset) throws IOException {
        var mipTex = WadMipTex.read(source);

        var surfaces = new ArrayList<Surface>();
        for (int i = 0; i < 4; i++) {
            var w = Math.max(1, mipTex.width() >> i);
            var h = Math.max(1, mipTex.height() >> i);
            surfaces.add(WadUtil.buildSurface(w, h, mipTex.mips().get(i), mipTex.palette(), mipTex.name().startsWith("{")));
        }
        return new Texture(mipTex.width(), mipTex.height(), TextureFormat.R8G8B8A8_UNORM, false, surfaces);
    }
}
