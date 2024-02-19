package be.twofold.valen.reader.md6anim;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.reader.*;
import be.twofold.valen.resource.*;

import java.util.*;
import java.util.stream.*;

public final class Md6AnimReader implements ResourceReader<Animation> {
    public Md6AnimReader() {
    }

    @Override
    public Animation read(BetterBuffer buffer, Resource resource) {
        var anim = Md6Anim.read(buffer);

        List<Track<?>> tracks = new ArrayList<>();
        tracks.addAll(mapRotations(anim));
        tracks.addAll(mapScales(anim));
        tracks.addAll(mapTranslations(anim));

        return new Animation(anim.data().frameRate(), tracks);
    }

    private List<Track<?>> mapRotations(Md6Anim anim) {
        var animMap = anim.animMaps().getFirst();

        List<KeyFrame<Quaternion>>[] rotations = listArray(findMaxBoneIndex(animMap.constR(), animMap.animR()) + 1);
        for (var i = 0; i < animMap.constR().length; i++) {
            rotations[animMap.constR()[i]].add(new KeyFrame<>(0, anim.constR().get(i)));
        }

        for (var frameSet : anim.frameSets()) {
            for (int i = 0, vi = 0; i < animMap.animR().length; i++) {
                var rotation = rotations[animMap.animR()[i]];
                rotation.add(new KeyFrame<>(frameSet.frameStart(), frameSet.firstR().get(i)));
                for (var frame = 0; frame < frameSet.frameRange(); frame++) {
                    if (frameSet.bitsR().get(i * frameSet.bytesPerBone() * 8 + frame)) {
                        rotation.add(new KeyFrame<>(frameSet.frameStart() + frame, frameSet.rangeR().get(vi++)));
                    }
                }
            }
        }

        return IntStream.range(0, rotations.length)
            .filter(i -> !rotations[i].isEmpty())
            .mapToObj(i -> new Track.Rotation(i, rotations[i]))
            .collect(Collectors.toList());
    }

    private List<Track<?>> mapScales(Md6Anim anim) {
        var animMap = anim.animMaps().getFirst();

        List<KeyFrame<Vector3>>[] scales = listArray(findMaxBoneIndex(animMap.constS(), animMap.animS()) + 1);
        for (var i = 0; i < animMap.constS().length; i++) {
            scales[animMap.constS()[i]].add(new KeyFrame<>(0, anim.constS().get(i)));
        }

        for (var frameSet : anim.frameSets()) {
            for (int i = 0, vi = 0; i < animMap.animS().length; i++) {
                var scale = scales[animMap.animS()[i]];
                scale.add(new KeyFrame<>(frameSet.frameStart(), frameSet.firstS().get(i)));
                for (var frame = 0; frame < frameSet.frameRange(); frame++) {
                    if (frameSet.bitsS().get(i * frameSet.bytesPerBone() * 8 + frame)) {
                        scale.add(new KeyFrame<>(frameSet.frameStart() + frame, frameSet.rangeS().get(vi++)));
                    }
                }
            }
        }

        return IntStream.range(0, scales.length)
            .filter(i -> !scales[i].isEmpty())
            .mapToObj(i -> new Track.Scale(i, scales[i]))
            .collect(Collectors.toList());
    }

    private List<Track<?>> mapTranslations(Md6Anim anim) {
        var animMap = anim.animMaps().getFirst();

        List<KeyFrame<Vector3>>[] translations = listArray(findMaxBoneIndex(animMap.constT(), animMap.animT()) + 1);
        for (var i = 0; i < animMap.constT().length; i++) {
            translations[animMap.constT()[i]].add(new KeyFrame<>(0, anim.constT().get(i)));
        }

        for (var frameSet : anim.frameSets()) {
            for (int i = 0, vi = 0; i < animMap.animT().length; i++) {
                var translation = translations[animMap.animT()[i]];
                translation.add(new KeyFrame<>(frameSet.frameStart(), frameSet.firstT().get(i)));
                for (var frame = 0; frame < frameSet.frameRange(); frame++) {
                    if (frameSet.bitsT().get(i * frameSet.bytesPerBone() * 8 + frame)) {
                        translation.add(new KeyFrame<>(frameSet.frameStart() + frame, frameSet.rangeT().get(vi++)));
                    }
                }
            }
        }

        return IntStream.range(0, translations.length)
            .filter(i -> !translations[i].isEmpty())
            .mapToObj(i -> new Track.Translation(i, translations[i]))
            .collect(Collectors.toList());
    }

    private int findMaxBoneIndex(int[] constBones, int[] animBones) {
        int constMax = Arrays.stream(constBones).max().orElse(-1);
        int animMax = Arrays.stream(animBones).max().orElse(-1);
        return Math.max(constMax, animMax);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T>[] listArray(int count) {
        List<T>[] lists = new List[count];
        for (var i = 0; i < lists.length; i++) {
            lists[i] = new ArrayList<>();
        }
        return lists;
    }
}
