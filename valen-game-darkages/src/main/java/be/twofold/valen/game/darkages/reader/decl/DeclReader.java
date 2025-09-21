package be.twofold.valen.game.darkages.reader.decl;

import be.twofold.valen.core.util.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.decl.*;

import java.util.*;

public final class DeclReader extends AbstractDeclReader<DarkAgesAssetID, DarkAgesAsset, DarkAgesArchive> {
    private static final String RootPrefix = "generated/decls/";

    private static final Set<String> Unsupported = Set.of(
        "animweb",
        "articulatedfigure",
        "breakable",
        "entitydef", // Custom content per entity
        "material2", // Has a custom reader
        "md6def",
        "renderlayerdefinition",
        "renderparm", // Has a custom reader
        "renderprogflag"
    );

    public DeclReader(DarkAgesArchive archive) {
        super(archive);
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        if (asset.id().type() != ResourcesType.RsStreamFile) {
            return false;
        }

        var name = asset.id().name().name();
        if (!name.startsWith(RootPrefix)) {
            return false;
        }

        var basePath = getBasePath(name);
        return !Unsupported.contains(basePath);
    }

    @Override
    public DarkAgesAssetID getAssetID(String name, DarkAgesAssetID baseAssetID) {
        String fullName;
        if (name.startsWith(RootPrefix)) {
            fullName = name;
        } else {
            var basePath = getBasePath(baseAssetID.fullName());
            fullName = RootPrefix + basePath + "/" + name + ".decl";
        }
        return DarkAgesAssetID.from(fullName, ResourcesType.RsStreamFile);
    }

    private String getBasePath(String name) {
        Check.argument(name.startsWith(RootPrefix), "Invalid decl name: " + name);

        name = name.substring(RootPrefix.length());
        return name.substring(0, name.indexOf('/'));
    }
}
