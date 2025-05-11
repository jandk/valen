package be.twofold.valen.export.cast.mappers;

import be.twofold.valen.core.animation.Animation;
import be.twofold.valen.core.animation.Track;
import be.twofold.valen.core.math.Quaternion;
import be.twofold.valen.core.math.Vector3;
import be.twofold.valen.format.cast.CastNode;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.Function;

public final class CastAnimationMapper {
    public long map(Animation animation, CastNode.Root root) {
        var animationNode = root.createAnimation()
                .setFramerate((float) animation.frameRate());

        var bones = animation.skeleton().bones();
        for (var track : animation.tracks()) {
            var boneName = bones.get(track.boneId()).name();
            mapCurve(animationNode, track, boneName);
        }
        return animationNode.hash();
    }

    private void mapCurve(CastNode.Animation animationNode, Track<?> track, String boneName) {
        switch (track) {
            case Track.Rotation rotation -> {
                createCurve(animationNode, boneName)
                        .setKeyPropertyName(CastNode.KeyPropertyName.RQ)
                        .setKeyFrameBuffer(frames(rotation))
                        .setKeyValueBufferV4(rotation(rotation));
            }
            case Track.Translation translation -> {
                var frames = frames(translation);
                createCurve(animationNode, boneName)
                        .setKeyPropertyName(CastNode.KeyPropertyName.TX)
                        .setKeyFrameBuffer(frames)
                        .setKeyValueBuffer(translationScale(translation, Vector3::x));
                createCurve(animationNode, boneName)
                        .setKeyPropertyName(CastNode.KeyPropertyName.TY)
                        .setKeyFrameBuffer(frames)
                        .setKeyValueBuffer(translationScale(translation, Vector3::y));
                createCurve(animationNode, boneName)
                        .setKeyPropertyName(CastNode.KeyPropertyName.TZ)
                        .setKeyFrameBuffer(frames)
                        .setKeyValueBuffer(translationScale(translation, Vector3::z));
            }
            case Track.Scale scale -> {
                var frames = frames(scale);
                createCurve(animationNode, boneName)
                        .setKeyPropertyName(CastNode.KeyPropertyName.SX)
                        .setKeyFrameBuffer(frames)
                        .setKeyValueBuffer(translationScale(scale, Vector3::x));
                createCurve(animationNode, boneName)
                        .setKeyPropertyName(CastNode.KeyPropertyName.SY)
                        .setKeyFrameBuffer(frames)
                        .setKeyValueBuffer(translationScale(scale, Vector3::y));
                createCurve(animationNode, boneName)
                        .setKeyPropertyName(CastNode.KeyPropertyName.SZ)
                        .setKeyFrameBuffer(frames)
                        .setKeyValueBuffer(translationScale(scale, Vector3::z));
            }
        }
    }

    private static CastNode.Curve createCurve(CastNode.Animation animationNode, String boneName) {
        return animationNode.createCurve()
                .setNodeName(boneName)
                .setMode(CastNode.Mode.ABSOLUTE);
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
