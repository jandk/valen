package be.twofold.valen.game.doom.readers.md6anim;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.doom.*;
import be.twofold.valen.game.doom.readers.common.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.md6anim.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.invoke.*;
import java.util.*;

public final class Md6AnimReader implements AssetReader.Binary<Animation, DoomAsset> {
    private static final Logger log = LoggerFactory.getLogger(Md6AnimReader.class);

    @Override
    public boolean canRead(DoomAsset asset) {
        return asset.id().type() == ResourceType.of("anim");
    }

    @Override
    public Animation read(BinaryReader reader, DoomAsset asset, LoadingContext context) throws IOException {
        var anim = Md6Anim.read(reader);

        var skeletonKey = new DoomAssetID(anim.header().skelName(), ResourceType.of("skeleton"));
        if (!context.exists(skeletonKey)) {
            log.warn("Could not find skeleton asset '{}' while loading anim '{}'", anim.header().skelName(), asset.id().fullName());
            throw new FileNotFoundException(anim.header().skelName());
        }
        var skeleton = context.load(skeletonKey, Skeleton.class);
        if (anim.animMaps().isEmpty()) {
            return new Animation(skeleton, anim.data().frameRate(), List.of());
        }

        var tracks = Md6AnimTracks.map(
            anim.animMaps().getFirst(),
            anim.constR(), anim.constS(), anim.constT(), anim.frameSets(),
            v -> v.multiply(Triangles.UNIT_SCALE));
        return new Animation(skeleton, anim.data().frameRate(), tracks);
    }

    @Override
    public Optional<Meta.Node> readMetadata(DoomAsset asset, LoadingContext context) throws IOException {
        try (var reader = BinaryReader.wrap(context.open(asset.location()))) {
            var anim = Md6Anim.read(reader);
            return Optional.of(Meta.build(MethodHandles.lookup(), anim));
        }
    }
}
