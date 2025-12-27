package be.twofold.valen.export.cast.mappers;

import be.twofold.tinycast.*;
import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.math.*;

import java.nio.*;
import java.util.function.*;

public final class CastAnimationMapper {
    public long map(Animation animation, CastNodes.Root root) {
        var animationNode = root.createAnimation()
                .setFramerate((float) animation.frameRate());

        var bones = animation.skeleton().bones();
        for (var track : animation.tracks()) {
            var boneName = bones.get(track.boneId()).name();
            mapCurve(animationNode, track, boneName);
        }
        return animationNode.getHash();
    }

    private void mapCurve(CastNodes.Animation animationNode, Track<?> track, String boneName) {
        switch (track) {
            case Track.Rotation rotation -> {
                createCurve(animationNode, boneName)
                    .setKeyPropertyName(CastNodes.KeyPropertyName.RQ)
                        .setKeyFrameBuffer(frames(rotation))
                        .setKeyValueBufferV4(rotation(rotation));
            }
            case Track.Translation translation -> {
                var frames = frames(translation);
                createCurve(animationNode, boneName)
                    .setKeyPropertyName(CastNodes.KeyPropertyName.TX)
                        .setKeyFrameBuffer(frames)
                    .setKeyValueBufferF32(translationScale(translation, Vector3::x));
                createCurve(animationNode, boneName)
                    .setKeyPropertyName(CastNodes.KeyPropertyName.TY)
                        .setKeyFrameBuffer(frames)
                    .setKeyValueBufferF32(translationScale(translation, Vector3::y));
                createCurve(animationNode, boneName)
                    .setKeyPropertyName(CastNodes.KeyPropertyName.TZ)
                        .setKeyFrameBuffer(frames)
                    .setKeyValueBufferF32(translationScale(translation, Vector3::z));
            }
            case Track.Scale scale -> {
                var frames = frames(scale);
                createCurve(animationNode, boneName)
                    .setKeyPropertyName(CastNodes.KeyPropertyName.SX)
                        .setKeyFrameBuffer(frames)
                    .setKeyValueBufferF32(translationScale(scale, Vector3::x));
                createCurve(animationNode, boneName)
                    .setKeyPropertyName(CastNodes.KeyPropertyName.SY)
                        .setKeyFrameBuffer(frames)
                    .setKeyValueBufferF32(translationScale(scale, Vector3::y));
                createCurve(animationNode, boneName)
                    .setKeyPropertyName(CastNodes.KeyPropertyName.SZ)
                        .setKeyFrameBuffer(frames)
                    .setKeyValueBufferF32(translationScale(scale, Vector3::z));
            }
        }
    }

    private static CastNodes.Curve createCurve(CastNodes.Animation animationNode, String boneName) {
        return animationNode.createCurve()
                .setNodeName(boneName)
            .setMode(CastNodes.Mode.ABSOLUTE);
    }

    private static FloatBuffer rotation(Track<Quaternion> rotation) {
        var result = FloatBuffer.allocate(rotation.keyFrames().size() * 4);
        rotation.keyFrames().forEach(frame -> frame.value().toBuffer(result));
        return result;
    }

    private static FloatBuffer translationScale(Track<Vector3> track, Function<Vector3, Float> mapper) {
        var result = FloatBuffer.allocate(track.keyFrames().size());
        track.keyFrames().forEach(frame -> result.put(mapper.apply(frame.value())));
        return result.rewind();
    }

    private static IntBuffer frames(Track<?> track) {
        var result = IntBuffer.allocate(track.keyFrames().size());
        track.keyFrames().forEach(frame -> result.put(frame.frame()));
        return result.rewind();
    }
}
