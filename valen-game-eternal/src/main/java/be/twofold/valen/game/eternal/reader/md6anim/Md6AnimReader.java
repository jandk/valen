package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.md6anim.*;
import org.slf4j.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.invoke.*;
import java.util.*;

public final class Md6AnimReader implements AssetReader.Binary<Animation, EternalAsset> {
    private static final Logger log = LoggerFactory.getLogger(Md6AnimReader.class);

    @Override
    public boolean canRead(EternalAsset asset) {
        return asset.id().type() == ResourceType.of("anim");
    }

    @Override
    public Animation read(BinaryReader reader, EternalAsset asset, LoadingContext context) throws IOException {
        var anim = Md6Anim.read(reader);

        var skeletonKey = EternalAssetID.from(anim.header().skelName(), ResourceType.of("skeleton"));
        if (!context.exists(skeletonKey)) {
            log.warn("Could not find skeleton asset '{}' while loading anim '{}'", anim.header().skelName(), asset.id().fullName());
            throw new FileNotFoundException(anim.header().skelName());
        }
        var skeleton = context.load(skeletonKey, Skeleton.class);

        var animMap = anim.animMaps().getFirst();

        var tracks = new ArrayList<Track<?>>();
        tracks.addAll(Md6AnimTracks.mapConstant(animMap.constR(), anim.constR(), Track.Rotation::new));
        tracks.addAll(Md6AnimTracks.mapConstant(animMap.constS(), anim.constS(), Track.Scale::new));
        tracks.addAll(Md6AnimTracks.mapConstant(animMap.constT(), anim.constT(), Track.Translation::new));

        tracks.addAll(Md6AnimTracks.mapAnimated(animMap.animR(), anim.frameSets(), FrameSet::bitsR, FrameSet::firstR, FrameSet::rangeR, Track.Rotation::new));
        tracks.addAll(Md6AnimTracks.mapAnimated(animMap.animS(), anim.frameSets(), FrameSet::bitsS, FrameSet::firstS, FrameSet::rangeS, Track.Scale::new));
        tracks.addAll(Md6AnimTracks.mapAnimated(animMap.animT(), anim.frameSets(), FrameSet::bitsT, FrameSet::firstT, FrameSet::rangeT, Track.Translation::new));
        return new Animation(skeleton, anim.data().frameRate(), tracks);
    }

    @Override
    public Optional<Meta.Node> readMetadata(EternalAsset asset, LoadingContext context) throws IOException {
        try (var reader = BinaryReader.wrap(context.open(asset.location()))) {
            var anim = Md6Anim.read(reader);
            return Optional.of(Meta.build(MethodHandles.lookup(), anim));
        }
    }
}
