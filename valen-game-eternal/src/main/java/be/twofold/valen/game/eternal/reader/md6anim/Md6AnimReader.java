package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.eternal.*;
import be.twofold.valen.game.eternal.resource.*;
import org.slf4j.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class Md6AnimReader implements AssetReader<Animation, EternalAsset> {
    private static final Logger log = LoggerFactory.getLogger(Md6AnimReader.class);

    private final EternalArchive archive;

    public Md6AnimReader(EternalArchive archive) {
        this.archive = archive;
    }

    @Override
    public boolean canRead(EternalAsset resource) {
        return resource.id().type() == ResourceType.Anim;
    }

    @Override
    public Animation read(BinarySource source, EternalAsset resource) throws IOException {
        var anim = Md6Anim.read(source);

        var skeletonKey = EternalAssetID.from(anim.header().skelName(), ResourceType.Skeleton);
        if (archive.get(skeletonKey).isEmpty()) {
            log.warn("Could not find skeleton asset '{}' while loading anim '{}'", anim.header().skelName(), resource.id().fullName());
            throw new FileNotFoundException(anim.header().skelName());
        }
        var skeleton = archive.loadAsset(skeletonKey, Skeleton.class);

        var animMap = anim.animMaps().getFirst();

        var tracks = new ArrayList<Track<?>>();
        tracks.addAll(mapConstant(animMap.constR(), anim.constR(), Track.Rotation::new));
        tracks.addAll(mapConstant(animMap.constS(), anim.constS(), Track.Scale::new));
        tracks.addAll(mapConstant(animMap.constT(), anim.constT(), Track.Translation::new));

        tracks.addAll(mapAnimated(animMap.animR(), anim.frameSets(), FrameSet::bitsR, FrameSet::firstR, FrameSet::rangeR, Track.Rotation::new));
        tracks.addAll(mapAnimated(animMap.animS(), anim.frameSets(), FrameSet::bitsS, FrameSet::firstS, FrameSet::rangeS, Track.Scale::new));
        tracks.addAll(mapAnimated(animMap.animT(), anim.frameSets(), FrameSet::bitsT, FrameSet::firstT, FrameSet::rangeT, Track.Translation::new));
        return new Animation(skeleton, anim.data().frameRate(), tracks);
    }

    private <T> List<Track<T>> mapConstant(
        Ints boneIDs,
        List<T> values,
        BiFunction<Integer, List<KeyFrame<T>>, Track<T>> constructor
    ) {
        return IntStream.range(0, boneIDs.length())
            .mapToObj(i -> constructor.apply(boneIDs.get(i), List.of(new KeyFrame<>(0, values.get(i)))))
            .toList();
    }

    private <T> List<Track<T>> mapAnimated(
        Ints animBoneIds,
        List<FrameSet> frameSets,
        Function<FrameSet, Bits> bitsMapper,
        Function<FrameSet, List<T>> firstMapper,
        Function<FrameSet, List<T>> rangeMapper,
        BiFunction<Integer, List<KeyFrame<T>>, Track<T>> constructor
    ) {
        var curves = new HashMap<Integer, List<KeyFrame<T>>>();

        for (var frameSet : frameSets) {
            var bits = bitsMapper.apply(frameSet);
            var first = firstMapper.apply(frameSet);
            var range = rangeMapper.apply(frameSet);

            for (int i = 0, vi = 0; i < animBoneIds.length(); i++) {
                var curve = curves.computeIfAbsent(animBoneIds.get(i), _ -> new ArrayList<>());
                curve.add(new KeyFrame<>(frameSet.frameStart(), first.get(i)));
                for (var frame = 0; frame < frameSet.frameRange(); frame++) {
                    if (bits.get(i * frameSet.bytesPerBone() * 8 + frame)) {
                        curve.add(new KeyFrame<>(frameSet.frameStart() + frame, range.get(vi++)));
                    }
                }
            }
        }

        return curves.entrySet().stream()
            .map(e -> constructor.apply(e.getKey(), e.getValue()))
            .toList();
    }
}
