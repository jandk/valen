package be.twofold.valen.game.doom.vmtr;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.doom.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public final class VmtrReader implements AssetReader<Texture, DoomAsset> {
    private final PageStitcher stitcher;

    public VmtrReader(PageStitcher stitcher) {
        this.stitcher = Check.nonNull(stitcher, "stitcher");
    }

    @Override
    public boolean canRead(DoomAsset asset) {
        return asset instanceof DoomAsset.Vmtr;
    }

    @Override
    public Texture read(DoomAsset asset, LoadingContext context) throws IOException {
        if (!(asset instanceof DoomAsset.Vmtr(var id, var entry))) {
            throw new IllegalArgumentException("Not a vmtr asset: " + asset);
        }

        return stitcher.read(entry, id.layer());
    }
}
