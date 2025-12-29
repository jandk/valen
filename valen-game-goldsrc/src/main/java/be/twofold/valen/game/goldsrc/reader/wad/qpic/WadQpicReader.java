package be.twofold.valen.game.goldsrc.reader.wad.qpic;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.goldsrc.*;
import be.twofold.valen.game.goldsrc.reader.wad.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class WadQpicReader implements AssetReader<Texture, GoldSrcAsset> {
    @Override
    public boolean canRead(GoldSrcAsset asset) {
        return asset instanceof GoldSrcAsset.Wad wadAsset
            && wadAsset.entry().type() == WadEntryType.QPIC;
    }

    @Override
    public Texture read(BinarySource source, GoldSrcAsset asset) throws IOException {
        var qpic = WadQpic.read(source);
        var surface = WadUtil.buildSurface(qpic.width(), qpic.height(), qpic.data(), qpic.palette(), false);
        return new Texture(qpic.width(), qpic.height(), TextureFormat.R8G8B8A8_UNORM, false, List.of(surface));
    }
}
