package be.twofold.valen.game.doom.vmtr;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.doom.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

/**
 * Reads virtual texture assets, by handing them to the {@link VmtrAtlas}.
 */
public final class VmtrReader implements AssetReader<Texture, DoomAsset> {
    private final VmtrAtlas atlas;

    public VmtrReader(VmtrAtlas atlas) {
        this.atlas = Check.nonNull(atlas, "atlas");
    }

    @Override
    public boolean canRead(DoomAsset asset) {
        return asset instanceof DoomAsset.Vmtr;
    }

    @Override
    public Texture read(DoomAsset asset, LoadingContext context) throws IOException {
        if (!(asset instanceof DoomAsset.Vmtr vmtr)) {
            throw new IllegalArgumentException("Not a vmtr asset: " + asset);
        }

        return atlas.read(vmtr.entry(), vmtr.id().layer());
    }
}
