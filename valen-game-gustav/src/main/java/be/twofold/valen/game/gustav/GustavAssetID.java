package be.twofold.valen.game.gustav;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.util.*;

public record GustavAssetID(
    String pakFile,
    String gtsFile
) implements AssetID {
    public GustavAssetID {
        Check.nonNull(pakFile, "pakFile");
    }

    public GustavAssetID(String pakFile) {
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
