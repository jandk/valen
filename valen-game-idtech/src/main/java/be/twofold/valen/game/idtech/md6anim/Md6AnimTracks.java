package be.twofold.valen.game.idtech.md6anim;

import be.twofold.valen.core.animation.*;
import wtf.reversed.toolbox.collect.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Maps decoded MD6 animation data to tracks. Bones that are {@link Md6AnimDecoder#MISSING} are skipped.
 */
public final class Md6AnimTracks {
    private Md6AnimTracks() {
    }

    public static <T> List<Track<T>> mapConstant(
        Ints boneIds,
        List<T> values,
        BiFunction<Integer, List<KeyFrame<T>>, Track<T>> constructor
    ) {
        return IntStream.range(0, boneIds.length())
            .filter(i -> boneIds.get(i) != Md6AnimDecoder.MISSING)
            .mapToObj(i -> constructor.apply(boneIds.get(i), List.of(new KeyFrame<>(0, values.get(i)))))
            .toList();
    }

    public static <T> List<Track<T>> mapAnimated(
        Ints boneIds,
        List<FrameSet> frameSets,
        Function<FrameSet, BitSet> bitsMapper,
        Function<FrameSet, List<T>> firstMapper,
        Function<FrameSet, List<T>> rangeMapper,
        BiFunction<Integer, List<KeyFrame<T>>, Track<T>> constructor
    ) {
        var curves = new LinkedHashMap<Integer, List<KeyFrame<T>>>();

        for (var frameSet : frameSets) {
            var bits = bitsMapper.apply(frameSet);
            var first = firstMapper.apply(frameSet);
            var range = rangeMapper.apply(frameSet);

            // Missing bones still have values, so they go through here to keep vi in sync
            for (int i = 0, vi = 0; i < boneIds.length(); i++) {
                var curve = curves.computeIfAbsent(boneIds.get(i), _ -> new ArrayList<>());
                curve.add(new KeyFrame<>(frameSet.frameStart(), first.get(i)));
                for (var frame = 0; frame < frameSet.frameRange(); frame++) {
                    if (bits.get(i * frameSet.bytesPerBone() * 8 + frame)) {
                        curve.add(new KeyFrame<>(frameSet.frameStart() + frame, range.get(vi++)));
                    }
                }
            }
        }

        return curves.entrySet().stream()
            .filter(e -> e.getKey() != Md6AnimDecoder.MISSING)
            .map(e -> constructor.apply(e.getKey(), e.getValue()))
            .toList();
    }
}
