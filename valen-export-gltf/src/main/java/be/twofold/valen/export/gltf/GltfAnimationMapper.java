package be.twofold.valen.export.gltf;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.export.gltf.model.*;

import java.nio.*;
import java.util.*;

final class GltfAnimationMapper {
    private final GltfContext context;

    GltfAnimationMapper(GltfContext context) {
        this.context = context;
    }

    AnimationSchema map(Animation animation, int skeletonNode) {
        List<AnimationSamplerSchema> samplers = new ArrayList<>();
        List<AnimationChannelSchema> channels = new ArrayList<>();

        for (var track : animation.tracks()) {
            switch (track) {
                case Track.Rotation rotation -> {
                    var keyFrameBuffer = buildKeyFrameBuffer(rotation.keyFrames(), animation.frameRate());
                    var rotationBuffer = buildRotationBuffer(rotation.keyFrames());

                    int keyFrameBufferView = context.createBufferView(keyFrameBuffer, keyFrameBuffer.capacity() * Float.BYTES, null);
                    int rotationBufferView = context.createBufferView(rotationBuffer, rotationBuffer.capacity() * Float.BYTES, null);

                    int input = buildAccessor(keyFrameBufferView, rotation.keyFrames().size(), min(keyFrameBuffer), max(keyFrameBuffer));
                    int output = buildAccessor(rotationBufferView, rotation.keyFrames().size(), AccessorType.Vector4);
                    samplers.add(new AnimationSamplerSchema(input, output));

                    var channelTargetSchema = new AnimationChannelTargetSchema(skeletonNode + rotation.boneId(), "rotation");
                    channels.add(new AnimationChannelSchema(samplers.size() - 1, channelTargetSchema));
                }
                case Track.Scale scale -> {
                    var keyFrameBuffer = buildKeyFrameBuffer(scale.keyFrames(), animation.frameRate());
                    var scaleBuffer = buildScaleTranslationBuffer(scale.keyFrames());

                    int keyFrameBufferView = context.createBufferView(keyFrameBuffer, keyFrameBuffer.capacity() * Float.BYTES, null);
                    int scaleBufferView = context.createBufferView(scaleBuffer, scaleBuffer.capacity() * Float.BYTES, null);

                    int input = buildAccessor(keyFrameBufferView, scale.keyFrames().size(), min(keyFrameBuffer), max(keyFrameBuffer));
                    int output = buildAccessor(scaleBufferView, scale.keyFrames().size(), AccessorType.Vector3);
                    samplers.add(new AnimationSamplerSchema(input, output));

                    var channelTargetSchema = new AnimationChannelTargetSchema(skeletonNode + scale.boneId(), "scale");
                    channels.add(new AnimationChannelSchema(samplers.size() - 1, channelTargetSchema));
                }
                case Track.Translation translation -> {
                    var keyFrameBuffer = buildKeyFrameBuffer(translation.keyFrames(), animation.frameRate());
                    var translationBuffer = buildScaleTranslationBuffer(translation.keyFrames());

                    int keyFrameBufferView = context.createBufferView(keyFrameBuffer, keyFrameBuffer.capacity() * Float.BYTES, null);
                    int translationBufferView = context.createBufferView(translationBuffer, translationBuffer.capacity() * Float.BYTES, null);

                    int input = buildAccessor(keyFrameBufferView, translation.keyFrames().size(), min(keyFrameBuffer), max(keyFrameBuffer));
                    int output = buildAccessor(translationBufferView, translation.keyFrames().size(), AccessorType.Vector3);
                    samplers.add(new AnimationSamplerSchema(input, output));

                    var channelTargetSchema = new AnimationChannelTargetSchema(skeletonNode + translation.boneId(), "translation");
                    channels.add(new AnimationChannelSchema(samplers.size() - 1, channelTargetSchema));
                }
            }
        }

        return new AnimationSchema("animation", channels, samplers);
    }


    private <T extends KeyFrame<?>> FloatBuffer buildKeyFrameBuffer(List<T> keyFrames, int frameRate) {
        var buffer = FloatBuffer.allocate(keyFrames.size());
        for (KeyFrame<?> keyFrame : keyFrames) {
            buffer.put((float) keyFrame.frame() / (float) frameRate);
        }
        return buffer.flip();
    }

    private FloatBuffer buildRotationBuffer(List<KeyFrame<Quaternion>> keyFrames) {
        var buffer = FloatBuffer.allocate(keyFrames.size() * 4);
        for (var keyFrame : keyFrames) {
            keyFrame.value().put(buffer);
        }
        return buffer.flip();
    }

    private FloatBuffer buildScaleTranslationBuffer(List<KeyFrame<Vector3>> keyFrames) {
        var buffer = FloatBuffer.allocate(keyFrames.size() * 3);
        for (var keyFrame : keyFrames) {
            keyFrame.value().put(buffer);
        }
        return buffer.flip();
    }

    private float[] min(FloatBuffer buffer) {
        float min = Float.POSITIVE_INFINITY;
        for (int i = 0; i < buffer.capacity(); i++) {
            min = Math.min(min, buffer.get(i));
        }
        return new float[]{min};
    }

    private float[] max(FloatBuffer buffer) {
        float max = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < buffer.capacity(); i++) {
            max = Math.max(max, buffer.get(i));
        }
        return new float[]{max};
    }

    private int buildAccessor(int bufferView, int count, AccessorType type) {
        AccessorSchema accessor = new AccessorSchema(
            bufferView,
            AccessorComponentType.Float,
            count,
            type,
            null,
            null,
            null
        );
        return context.addAccessor(accessor);
    }

    private int buildAccessor(int bufferView, int count, float[] min, float[] max) {
        AccessorSchema accessor = new AccessorSchema(
            bufferView,
            AccessorComponentType.Float,
            count,
            AccessorType.Scalar,
            min,
            max,
            null
        );
        return context.addAccessor(accessor);
    }
}
