package be.twofold.valen.game.gbfr;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.util.*;

public record GbfrAssetID(
    String pakFile,
    String gtsFile
) implements AssetID {
    public GbfrAssetID {
        Check.nonNull(pakFile, "pakFile");
    }

    public GbfrAssetID(String pakFile) {
        this(pakFile, null);
    }

    @Override
    public String fullName() {
        if (gtsFile == null) {
            return pakFile;
        }
        return Filenames.removeExtension(pakFile) + "/" + gtsFile;
    }

    public boolean isGts() {
        return gtsFile != null;
    }
}
