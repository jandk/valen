package be.twofold.valen.game.eternal.reader.decl;

import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.idtech.decl.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class DeclReader extends AbstractDeclReader<EternalAssetID, EternalAsset, EternalArchive> {
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

    public DeclReader(EternalArchive archive) {
        super(archive);
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        if (resource.id().type() != ResourceType.RsStreamFile) {
            return false;
        }

        var name = resource.id().name().name();
        if (!name.startsWith(RootPrefix)) {
            return false;
        }

        var basePath = getBasePath(name);
        return !Unsupported.contains(basePath);
    }

    @Override
    public EternalAssetID getAssetID(String name, EternalAssetID baseAssetID) {
        String fullName;
        if (name.startsWith(RootPrefix)) {
            fullName = name;
        } else {
            var basePath = getBasePath(baseAssetID.fullName());
            fullName = RootPrefix + basePath + "/" + name + ".decl";
        }
        return EternalAssetID.from(fullName, ResourceType.RsStreamFile);
    }

    private String getBasePath(String name) {
        Check.argument(name.startsWith(RootPrefix), "Invalid decl name: " + name);

        name = name.substring(RootPrefix.length());
        return name.substring(0, name.indexOf('/'));
    }
}
