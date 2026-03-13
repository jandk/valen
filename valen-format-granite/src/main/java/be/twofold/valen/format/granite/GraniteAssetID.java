package be.twofold.valen.format.granite;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.util.*;

public record GraniteAssetID(
    String gtsFile,
    String gtsEntry
) implements AssetID {
    public GraniteAssetID {
        Check.nonNull(gtsFile, "gtsFile");
        Check.nonNull(gtsEntry, "gtsEntry");
    }

    @Override
    public String fullName() {
        return Filenames.removeExtension(gtsFile) + "/" + gtsEntry;
    }
}
