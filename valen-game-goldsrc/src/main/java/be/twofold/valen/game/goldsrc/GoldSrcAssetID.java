package be.twofold.valen.game.goldsrc;

import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.util.*;

public record GoldSrcAssetID(
    String baseName,
    String entryName
) implements AssetID {
    public GoldSrcAssetID {
        Check.nonNull(baseName, "baseName");
    }

    public GoldSrcAssetID(String baseName) {
        this(baseName, null);
    }

    @Override
    public String fullName() {
        if (entryName == null) {
            return baseName;
        }
        return baseName + "/" + entryName;
    }

    @Override
    public String displayName() {
        return fileName();
    }

    public boolean isWad() {
        return baseName.endsWith(".wad") && entryName != null;
    }
}
