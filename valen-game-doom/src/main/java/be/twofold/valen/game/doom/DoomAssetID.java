package be.twofold.valen.game.doom;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.doom.vmtr.*;
import wtf.reversed.toolbox.util.*;

public record DoomAssetID(
    String name,
    VmtrLayer layer
) implements AssetID {
    public DoomAssetID {
        Check.nonNull(name, "name");
    }

    public DoomAssetID(String name) {
        this(name, null);
    }

    @Override
    public String fullName() {
        return layer == null ? name : name + layer.suffix();
    }

    public boolean isVmtr() {
        return layer != null;
    }
}
