package be.twofold.valen.game.darkages.reader.decl;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.idtech.reader.decl.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

public final class DeclReader extends AbstractDeclReader<DarkAgesAssetID, DarkAgesAsset> {
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

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        if (asset.id().type() != ResourcesType.RsStreamFile) {
            return false;
        }

        var path = asset.id().path();
        return path.startsWith(AssetPaths.DECLS)
            && !Unsupported.contains(getBasePath(path));
    }

    @Override
    public DarkAgesAssetID getAssetID(String name, DarkAgesAssetID baseAssetID) {
        var path = AssetPath.of(name);
        if (!path.startsWith(AssetPaths.DECLS)) {
            path = AssetPaths.DECLS
                .resolve(getBasePath(baseAssetID.path()))
                .resolve(name + ".decl");
        }
        return DarkAgesAssetID.from(path.value(), ResourcesType.RsStreamFile);
    }

    private static String getBasePath(AssetPath path) {
        var segments = path.relativize(AssetPaths.DECLS).segments();
        Check.argument(segments.size() > 1, () -> "Invalid decl name: " + path);
        return segments.getFirst();
    }
}
