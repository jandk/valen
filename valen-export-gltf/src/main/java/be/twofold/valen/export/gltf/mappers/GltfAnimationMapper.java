package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.export.gltf.*;
import be.twofold.valen.export.gltf.model.*;

import java.nio.*;
import java.util.*;

public final class GltfAnimationMapper {
    private final GltfContext context;

    public GltfAnimationMapper(GltfContext context) {
        this.context = context;
    }

    AnimationSchema map(Animation animation, NodeId skeletonNode) {
        List<AnimationSamplerSchema> samplers = new ArrayList<>();
        List<AnimationChannelSchema> channels = new ArrayList<>();

        for (var track : animation.tracks()) {
            switch (track) {
                case Track.Rotation rotation -> {
                    var keyFrameBuffer = buildKeyFrameBuffer(rotation.keyFrames(), animation.frameRate());
                    var rotationBuffer = buildRotationBuffer(rotation.keyFrames());

                    var keyFrameBufferView = context.createBufferView(keyFrameBuffer, keyFrameBuffer.capacity() * Float.BYTES, null);
                    var rotationBufferView = context.createBufferView(rotationBuffer, rotationBuffer.capacity() * Float.BYTES, null);

                    var input = buildAccessor(keyFrameBufferView, rotation.keyFrames().size(), min(keyFrameBuffer), max(keyFrameBuffer));
                    var output = buildAccessor(rotationBufferView, rotation.keyFrames().size(), AccessorType.Vector4);
                    samplers.add(AnimationSamplerSchema.builder().input(input).output(output).build());

                    var channelTargetSchema = animationChannelTarget(skeletonNode, rotation, AnimationChannelTargetPath.Rotation);
                    channels.add(AnimationChannelSchema.builder().sampler(AnimationSamplerId.of(samplers.size() - 1)).target(channelTargetSchema).build());
                }
                case Track.Scale scale -> {
                    var keyFrameBuffer = buildKeyFrameBuffer(scale.keyFrames(), animation.frameRate());
                    var scaleBuffer = buildScaleTranslationBuffer(scale.keyFrames());

                    var keyFrameBufferView = context.createBufferView(keyFrameBuffer, keyFrameBuffer.capacity() * Float.BYTES, null);
                    var scaleBufferView = context.createBufferView(scaleBuffer, scaleBuffer.capacity() * Float.BYTES, null);

                    var input = buildAccessor(keyFrameBufferView, scale.keyFrames().size(), min(keyFrameBuffer), max(keyFrameBuffer));
                    var output = buildAccessor(scaleBufferView, scale.keyFrames().size(), AccessorType.Vector3);
                    samplers.add(AnimationSamplerSchema.builder().input(input).output(output).build());

                    var channelTargetSchema = animationChannelTarget(skeletonNode, scale, AnimationChannelTargetPath.Scale);
                    channels.add(AnimationChannelSchema.builder().sampler(AnimationSamplerId.of(samplers.size() - 1)).target(channelTargetSchema).build());
                }
                case Track.Translation translation -> {
                    var keyFrameBuffer = buildKeyFrameBuffer(translation.keyFrames(), animation.frameRate());
                    var translationBuffer = buildScaleTranslationBuffer(translation.keyFrames());

                    var keyFrameBufferView = context.createBufferView(keyFrameBuffer, keyFrameBuffer.capacity() * Float.BYTES, null);
                    var translationBufferView = context.createBufferView(translationBuffer, translationBuffer.capacity() * Float.BYTES, null);

                    var input = buildAccessor(keyFrameBufferView, translation.keyFrames().size(), min(keyFrameBuffer), max(keyFrameBuffer));
                    var output = buildAccessor(translationBufferView, translation.keyFrames().size(), AccessorType.Vector3);
                    samplers.add(AnimationSamplerSchema.builder().input(input).output(output).build());

                    var channelTargetSchema = animationChannelTarget(skeletonNode, translation, AnimationChannelTargetPath.Translation);
                    channels.add(AnimationChannelSchema.builder().sampler(AnimationSamplerId.of(samplers.size() - 1)).target(channelTargetSchema).build());
                }
            }
        }

        return AnimationSchema.builder()
            .name("animation")
            .channels(channels)
            .samplers(samplers)
            .build();
    }

    private static AnimationChannelTargetSchema animationChannelTarget(
        NodeId skeletonNode,
        Track<?> translation,
        AnimationChannelTargetPath path
    ) {
        return AnimationChannelTargetSchema.builder()
            .node(skeletonNode.add(translation.boneId()))
            .path(path)
            .build();
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
        var min = Float.POSITIVE_INFINITY;
        for (var i = 0; i < buffer.capacity(); i++) {
            min = Math.min(min, buffer.get(i));
        }
        return new float[]{min};
    }

    private float[] max(FloatBuffer buffer) {
        var max = Float.NEGATIVE_INFINITY;
        for (var i = 0; i < buffer.capacity(); i++) {
            max = Math.max(max, buffer.get(i));
        }
        return new float[]{max};
    }

    private AccessorId buildAccessor(BufferViewId bufferView, int count, AccessorType type) {
        var accessor = AccessorSchema.builder()
            .bufferView(bufferView)
            .componentType(AccessorComponentType.Float)
            .count(count)
            .type(type)
            .build();
        return context.addAccessor(accessor);
    }

    private AccessorId buildAccessor(BufferViewId bufferView, int count, float[] min, float[] max) {
        var accessor = AccessorSchema.builder()
            .bufferView(bufferView)
            .componentType(AccessorComponentType.Float)
            .count(count)
            .type(AccessorType.Scalar)
            .min(min)
            .max(max)
            .build();
        return context.addAccessor(accessor);
    }
}
