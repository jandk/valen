package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;
import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.*;
import be.twofold.valen.game.darkages.reader.resources.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class Md6AnimReader implements AssetReader<Animation, DarkAgesAsset> {
    private final DarkAgesArchive archive;

    public Md6AnimReader(DarkAgesArchive archive) {
        this.archive = Check.notNull(archive, "archive");
    }

    @Override
    public boolean canRead(DarkAgesAsset asset) {
        return asset.id().type() == ResourcesType.Anim;
    }

    @Override
    public Animation read(BinaryReader reader, DarkAgesAsset asset) throws IOException {
        var anim = Md6Anim.read(reader);
        var skeleton = archive.loadAsset(DarkAgesAssetID.from(anim.header().skelName(), ResourcesType.Skeleton), Skeleton.class);

        var frameSets = switch (anim.data().streamMethod()) {
            case UNSTREAMED -> readUnstreamed(reader, anim.frameSetOffsetTable(), anim.map());
            case UNSTREAMED_FIRST_FRAMESET -> throw new UnsupportedOperationException("UNSTREAMED_FIRST_FRAMESET");
            case STREAMED -> readStreamed(anim.streamInfo(), anim.map(), asset.hash());
            case LODS -> readStreamed(anim.streamInfo(), anim.map(), asset.hash());
        };

        var animMap = anim.map();
        var tracks = new ArrayList<Track<?>>();
        tracks.addAll(mapConstant(animMap.constR(), anim.constR(), Track.Rotation::new));
        tracks.addAll(mapConstant(animMap.constS(), anim.constS(), Track.Scale::new));
        tracks.addAll(mapConstant(animMap.constT(), anim.constT(), Track.Translation::new));

        tracks.addAll(mapAnimated(animMap.animR(), frameSets, FrameSet::bitsR, FrameSet::firstR, FrameSet::rangeR, Track.Rotation::new));
        tracks.addAll(mapAnimated(animMap.animS(), frameSets, FrameSet::bitsS, FrameSet::firstS, FrameSet::rangeS, Track.Scale::new));
        tracks.addAll(mapAnimated(animMap.animT(), frameSets, FrameSet::bitsT, FrameSet::firstT, FrameSet::rangeT, Track.Translation::new));
        return new Animation(skeleton, anim.data().frameRate(), tracks);
    }

    private List<FrameSet> readUnstreamed(BinaryReader reader, int[] frameSetOffsetTable, Md6AnimMap animMap) throws IOException {
        var start = reader.position();
        var frameSets = new ArrayList<FrameSet>();
        for (var i = 0; i < frameSetOffsetTable.length - 1; i++) {
            var frameSetOffset = start + frameSetOffsetTable[i] * 16L;
            var frameSet = reader.position(frameSetOffset).readObject(s -> FrameSet.read(s, frameSetOffset, animMap));
            frameSets.add(frameSet);
        }
        return frameSets;
    }

    private List<FrameSet> readStreamed(Md6AnimStreamInfo streamInfo, Md6AnimMap animMap, long hash) throws IOException {
        var sources = new BinaryReader[streamInfo.layouts().size()];
        var frameSets = new ArrayList<FrameSet>();
        for (int i = 0; i < streamInfo.framsetToStreamLayout().length; i++) {
            var layoutIndex = streamInfo.framsetToStreamLayout()[i];
            if (sources[layoutIndex] == null) {
                var streamHash = Hash.hash(hash, 0, layoutIndex);
                var bytes = archive.readStream(streamHash, streamInfo.layouts().get(layoutIndex).uncompressedSize());
                sources[layoutIndex] = BinaryReader.fromBytes(bytes);
            }

            var frameSetOffset = Short.toUnsignedInt(streamInfo.streamFrameSetOffsets()[i]);
            var frameSet = FrameSet.read(sources[layoutIndex], frameSetOffset, animMap);
            frameSets.add(frameSet);
        }
        return frameSets;
    }


    private <T> List<Track<T>> mapConstant(
        int[] boneIDs,
        List<T> values,
        BiFunction<Integer, List<KeyFrame<T>>, Track<T>> constructor
    ) {
        return IntStream.range(0, boneIDs.length)
            .mapToObj(i -> constructor.apply(boneIDs[i], List.of(new KeyFrame<>(0, values.get(i)))))
            .toList();
    }

    private <T> List<Track<T>> mapAnimated(
        int[] animBoneIds,
        List<FrameSet> frameSets,
        Function<FrameSet, Bytes> bitsMapper,
        Function<FrameSet, List<T>> firstMapper,
        Function<FrameSet, List<T>> rangeMapper,
        BiFunction<Integer, List<KeyFrame<T>>, Track<T>> constructor
    ) {
        var curves = new LinkedHashMap<Integer, List<KeyFrame<T>>>();

        for (var frameSet : frameSets) {
            var bits = bitsMapper.apply(frameSet);
            var first = firstMapper.apply(frameSet);
            var range = rangeMapper.apply(frameSet);
            int bytesPerBone = frameSet.bytesPerBone();

            for (int i = 0, vi = 0; i < animBoneIds.length; i++) {
                var curve = curves.computeIfAbsent(animBoneIds[i], _ -> new ArrayList<>());
                curve.add(new KeyFrame<>(frameSet.frameStart(), first.get(i)));
                for (var frame = 0; frame < frameSet.frameRange(); frame++) {
                    if (checkFrameLE(bits, i, bytesPerBone, frame)) {
                        curve.add(new KeyFrame<>(frameSet.frameStart() + frame, range.get(vi++)));
                    }
                }
            }
        }

        return curves.entrySet().stream()
            .map(e -> constructor.apply(e.getKey(), e.getValue()))
            .toList();
    }

    // Standard anims use this
    private boolean checkFrameBE(Bytes bytes, int boneId, int bytesPerBone, int frame) {
        int frameByte = frame >> 3;
        int byteIndex = boneId * bytesPerBone + frameByte;
        int bitIndex = 0x80 >> (frame & 7);
        return (bytes.getByte(byteIndex) & bitIndex) != 0;
    }

    // LODS use this
    private boolean checkFrameLE(Bytes bytes, int boneId, int bytesPerBone, int frame) {
        int frameByte = bytesPerBone - (frame >> 3) - 1;
        int byteIndex = boneId * bytesPerBone + frameByte;
        int bitIndex = 0x80 >> (frame & 7);
        return (bytes.getByte(byteIndex) & bitIndex) != 0;
    }
}
