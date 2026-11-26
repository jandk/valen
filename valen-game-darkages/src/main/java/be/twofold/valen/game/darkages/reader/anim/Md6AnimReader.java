package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.idtech.game.*;
import be.twofold.valen.game.idtech.md6anim.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.lang.invoke.*;
import java.util.*;

public final class Md6AnimReader implements AssetReader.Binary<Animation, DarkAgesAsset> {
    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourceType.of("anim");
    }

    @Override
    public Animation read(BinaryReader reader, DarkAgesAsset asset, LoadingContext context) throws IOException {
        var anim = Md6Anim.read(reader);
        var skeleton = context.load(DarkAgesAssetID.from(anim.header().skelName(), ResourceType.of("skeleton")), Skeleton.class);

        var frameSets = switch (anim.data().streamMethod()) {
            case UNSTREAMED -> readUnstreamed(reader, anim.frameSetOffsetTable(), anim.map());
            case UNSTREAMED_FIRST_FRAMESET -> throw new UnsupportedOperationException("UNSTREAMED_FIRST_FRAMESET");
            case STREAMED -> readStreamed(anim.streamInfo(), anim.map(), asset.hash(), context);
            case LODS -> readStreamed(anim.streamInfo(), anim.map(), asset.hash(), context);
        };

        var animMap = anim.map();
        var tracks = new ArrayList<Track<?>>();
        tracks.addAll(Md6AnimTracks.mapConstant(animMap.constR(), anim.constR(), Track.Rotation::new));
        tracks.addAll(Md6AnimTracks.mapConstant(animMap.constS(), anim.constS(), Track.Scale::new));
        tracks.addAll(Md6AnimTracks.mapConstant(animMap.constT(), anim.constT(), Track.Translation::new));

        tracks.addAll(Md6AnimTracks.mapAnimated(animMap.animR(), frameSets, FrameSet::bitsR, FrameSet::firstR, FrameSet::rangeR, Track.Rotation::new));
        tracks.addAll(Md6AnimTracks.mapAnimated(animMap.animS(), frameSets, FrameSet::bitsS, FrameSet::firstS, FrameSet::rangeS, Track.Scale::new));
        tracks.addAll(Md6AnimTracks.mapAnimated(animMap.animT(), frameSets, FrameSet::bitsT, FrameSet::firstT, FrameSet::rangeT, Track.Translation::new));
        return new Animation(skeleton, anim.data().frameRate(), tracks);
    }

    @Override
    public Optional<Meta.Node> readMetadata(DarkAgesAsset asset, LoadingContext context) throws IOException {
        try (var reader = BinaryReader.wrap(context.open(asset.location()))) {
            var anim = Md6Anim.read(reader);
            return Optional.of(Meta.build(MethodHandles.lookup(), anim));
        }
    }

    private List<FrameSet> readUnstreamed(BinaryReader reader, Ints frameSetOffsetTable, Md6AnimMap animMap) throws IOException {
        var start = reader.position();
        var frameSets = new ArrayList<FrameSet>();
        for (var i = 0; i < frameSetOffsetTable.length() - 1; i++) {
            var frameSetOffset = start + frameSetOffsetTable.get(i) * 16L;
            var frameSet = reader.position(frameSetOffset).readObject(s -> FrameSet.read(s, frameSetOffset, animMap.animR().length(), animMap.animS().length(), animMap.animT().length()));
            frameSets.add(frameSet);
        }
        return frameSets;
    }

    private List<FrameSet> readStreamed(Md6AnimStreamInfo streamInfo, Md6AnimMap animMap, long hash, LoadingContext context) throws IOException {
        var sources = new BinaryReader[streamInfo.layouts().size()];
        var frameSets = new ArrayList<FrameSet>();
        for (int i = 0; i < streamInfo.framsetToStreamLayout().length(); i++) {
            var layoutIndex = streamInfo.framsetToStreamLayout().get(i);
            if (sources[layoutIndex] == null) {
                var streamHash = Hash.hash(hash, 0, layoutIndex);
                int size = streamInfo.layouts().get(layoutIndex).uncompressedSize();
                var bytes = context.open(new DarkAgesStreamLocation(streamHash, size));
                sources[layoutIndex] = BinaryReader.wrap(bytes);
            }

            var frameSetOffset = Short.toUnsignedInt(streamInfo.streamFrameSetOffsets().get(i));
            var frameSet = FrameSet.read(sources[layoutIndex], frameSetOffset, animMap.animR().length(), animMap.animS().length(), animMap.animT().length());
            frameSets.add(frameSet);
        }
        return frameSets;
    }
}
