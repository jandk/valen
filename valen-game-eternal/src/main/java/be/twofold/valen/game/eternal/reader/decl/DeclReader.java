package be.twofold.valen.game.eternal.reader.decl;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.idtech.decl.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class DeclReader extends AbstractDeclReader<EternalAssetID, EternalAsset> {
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

    public DeclReader() {
        super();
    }

    @Override
    public boolean canRead(EternalAsset asset) {
        if (asset.id().type() != ResourceType.RsStreamFile) {
            return false;
        }

        var path = asset.id().path();
        return path.startsWith(AssetPaths.DECLS)
            && !Unsupported.contains(getBasePath(path));
    }

    @Override
    public EternalAssetID getAssetID(String name, EternalAssetID baseAssetID) {
        var path = AssetPath.of(name);
        if (!path.startsWith(AssetPaths.DECLS)) {
            path = AssetPaths.DECLS
                .resolve(getBasePath(baseAssetID.path()))
                .resolve(name + ".decl");
        }
        return EternalAssetID.from(path.value(), ResourceType.RsStreamFile);
    }

    private static String getBasePath(AssetPath path) {
        var segments = path.relativize(AssetPaths.DECLS).segments();
        Check.argument(segments.size() > 1, () -> "Invalid decl name: " + path);
        return segments.getFirst();
    }
}
