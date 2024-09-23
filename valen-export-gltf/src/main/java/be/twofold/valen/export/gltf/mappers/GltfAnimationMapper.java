package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.gltf.*;
import be.twofold.valen.gltf.model.accessor.*;
import be.twofold.valen.gltf.model.animation.*;
import be.twofold.valen.gltf.model.buffer.*;
import be.twofold.valen.gltf.model.node.*;

import java.nio.*;
import java.util.*;

public final class GltfAnimationMapper {
    private final GltfContext context;

    public GltfAnimationMapper(GltfContext context) {
        this.context = context;
    }

    public AnimationSchema map(Animation animation, int numBones) {
        List<AnimationSamplerSchema> samplers = new ArrayList<>();
        List<AnimationChannelSchema> channels = new ArrayList<>();

        for (var track : animation.tracks()) {
            if (track.boneId() >= numBones) {
                continue;
            }
            switch (track) {
                case Track.Rotation rotation -> {
                    var keyFrameBuffer = buildKeyFrameBuffer(rotation.keyFrames(), animation.frameRate());
                    var rotationBuffer = buildRotationBuffer(rotation.keyFrames());

                    var keyFrameBufferView = context.createBufferView(keyFrameBuffer, keyFrameBuffer.capacity() * Float.BYTES, null);
                    var rotationBufferView = context.createBufferView(rotationBuffer, rotationBuffer.capacity() * Float.BYTES, null);

                    var input = buildAccessor(keyFrameBufferView, rotation.keyFrames().size(), min(keyFrameBuffer), max(keyFrameBuffer));
                    var output = buildAccessor(rotationBufferView, rotation.keyFrames().size(), AccessorType.VEC4);
                    samplers.add(AnimationSamplerSchema.builder().input(input).output(output).build());

                    var channelTargetSchema = animationChannelTarget(rotation, AnimationChannelTargetPath.ROTATION);
                    channels.add(AnimationChannelSchema.builder().sampler(AnimationSamplerID.of(samplers.size() - 1)).target(channelTargetSchema).build());
                }
                case Track.Scale scale -> {
                    var keyFrameBuffer = buildKeyFrameBuffer(scale.keyFrames(), animation.frameRate());
                    var scaleBuffer = buildScaleTranslationBuffer(scale.keyFrames());

                    var keyFrameBufferView = context.createBufferView(keyFrameBuffer, keyFrameBuffer.capacity() * Float.BYTES, null);
                    var scaleBufferView = context.createBufferView(scaleBuffer, scaleBuffer.capacity() * Float.BYTES, null);

                    var input = buildAccessor(keyFrameBufferView, scale.keyFrames().size(), min(keyFrameBuffer), max(keyFrameBuffer));
                    var output = buildAccessor(scaleBufferView, scale.keyFrames().size(), AccessorType.VEC3);
                    samplers.add(AnimationSamplerSchema.builder().input(input).output(output).build());

                    var channelTargetSchema = animationChannelTarget(scale, AnimationChannelTargetPath.SCALE);
                    channels.add(AnimationChannelSchema.builder().sampler(AnimationSamplerID.of(samplers.size() - 1)).target(channelTargetSchema).build());
                }
                case Track.Translation translation -> {
                    var keyFrameBuffer = buildKeyFrameBuffer(translation.keyFrames(), animation.frameRate());
                    var translationBuffer = buildScaleTranslationBuffer(translation.keyFrames());

                    var keyFrameBufferView = context.createBufferView(keyFrameBuffer, keyFrameBuffer.capacity() * Float.BYTES, null);
                    var translationBufferView = context.createBufferView(translationBuffer, translationBuffer.capacity() * Float.BYTES, null);

                    var input = buildAccessor(keyFrameBufferView, translation.keyFrames().size(), min(keyFrameBuffer), max(keyFrameBuffer));
                    var output = buildAccessor(translationBufferView, translation.keyFrames().size(), AccessorType.VEC3);
                    samplers.add(AnimationSamplerSchema.builder().input(input).output(output).build());

                    var channelTargetSchema = animationChannelTarget(translation, AnimationChannelTargetPath.TRANSLATION);
                    channels.add(AnimationChannelSchema.builder().sampler(AnimationSamplerID.of(samplers.size() - 1)).target(channelTargetSchema).build());
                }
            }
        }

        return AnimationSchema.builder()
            .channels(channels)
            .samplers(samplers)
            .build();
    }

    private static AnimationChannelTargetSchema animationChannelTarget(
        Track<?> track,
        AnimationChannelTargetPath path
    ) {
        return AnimationChannelTargetSchema.builder()
            .node(NodeID.of(track.boneId()))
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

    private AccessorID buildAccessor(BufferViewID bufferView, int count, AccessorType type) {
        var accessor = AccessorSchema.builder()
            .bufferView(bufferView)
            .componentType(AccessorComponentType.FLOAT)
            .count(count)
            .type(type)
            .build();
        return context.addAccessor(accessor);
    }

    private AccessorID buildAccessor(BufferViewID bufferView, int count, float[] min, float[] max) {
        var accessor = AccessorSchema.builder()
            .bufferView(bufferView)
            .componentType(AccessorComponentType.FLOAT)
            .count(count)
            .type(AccessorType.SCALAR)
            .min(min)
            .max(max)
            .build();
        return context.addAccessor(accessor);
    }
}
