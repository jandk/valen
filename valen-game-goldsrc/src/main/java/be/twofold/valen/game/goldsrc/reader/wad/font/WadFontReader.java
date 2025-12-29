package be.twofold.valen.game.goldsrc.reader.wad.font;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.goldsrc.*;
import be.twofold.valen.game.goldsrc.reader.wad.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class WadFontReader implements AssetReader<Texture, GoldSrcAsset> {
    @Override
    public boolean canRead(GoldSrcAsset asset) {
        return asset instanceof GoldSrcAsset.Wad wadAsset
            && wadAsset.entry().type() == WadEntryType.FONT;
    }

    @Override
    public Texture read(BinarySource source, GoldSrcAsset asset) throws IOException {
        var font = WadFont.read(source);
        var surface = WadUtil.buildSurface(256, font.height(), font.data(), font.palette(), true);
        return new Texture(256, font.height(), TextureFormat.R8G8B8A8_UNORM, false, List.of(surface));
    }
}
