package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.animation.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.accessor.*;
import be.twofold.valen.format.gltf.model.animation.*;
import be.twofold.valen.format.gltf.model.bufferview.*;
import be.twofold.valen.format.gltf.model.node.*;
import be.twofold.valen.format.gltf.types.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.function.*;

public final class GltfAnimationMapper {
    private final List<AnimationSamplerSchema> samplers = new ArrayList<>();
    private final List<AnimationChannelSchema> channels = new ArrayList<>();
    private final Map<List<Integer>, AccessorID> tracks = new HashMap<>();
    private final GltfContext context;

    public GltfAnimationMapper(GltfContext context) {
        this.context = context;
    }

    public AnimationSchema map(Animation animation, int numBones) throws IOException {
        for (var track : animation.tracks()) {
            if (track.boneId() >= numBones) {
                continue;
            }
            var frameRate = animation.frameRate();
            switch (track) {
                case Track.Rotation r ->
                    buildSampler(frameRate, r, AccessorType.VEC4, AnimationChannelTargetPath.ROTATION, Quaternion::toBuffer);
                case Track.Scale s ->
                    buildSampler(frameRate, s, AccessorType.VEC3, AnimationChannelTargetPath.SCALE, Vector3::toBuffer);
                case Track.Translation t ->
                    buildSampler(frameRate, t, AccessorType.VEC3, AnimationChannelTargetPath.TRANSLATION, Vector3::toBuffer);
            }
        }

        return ImmutableAnimation.builder()
            .channels(channels)
            .samplers(samplers)
            .build();
    }

    private <T> void buildSampler(
        int frameRate,
        Track<T> track,
        AccessorType type,
        AnimationChannelTargetPath path,
        BiConsumer<T, FloatBuffer> toBuffer
    ) throws IOException {
        var keyFrames = track.keyFrames().stream().map(KeyFrame::frame).toList();
        var input = tracks.get(keyFrames);
        if (input == null) {
            var inputBuffer = buildInputBuffer(track.keyFrames(), frameRate);
            var inputBufferView = context.createBufferView(inputBuffer, null);
            input = buildAccessor(inputBufferView, track.keyFrames().size(), AccessorType.SCALAR,
                (float) track.keyFrames().getFirst().frame() / (float) frameRate,
                (float) track.keyFrames().getLast().frame() / (float) frameRate);
            tracks.put(keyFrames, input);
        }

        var outputBuffer = buildOutputBuffer(track.keyFrames(), toBuffer);
        var outputBufferView = context.createBufferView(outputBuffer, null);
        var output = buildAccessor(outputBufferView, track.keyFrames().size(), type, null, null);

        samplers.add(ImmutableAnimationSampler.builder()
            .input(input)
            .output(output)
            .build());

        channels.add(ImmutableAnimationChannel.builder()
            .sampler(AnimationSamplerID.of(samplers.size() - 1))
            .target(ImmutableAnimationChannelTarget.builder()
                .node(NodeID.of(track.boneId()))
                .path(path)
                .build())
            .build());
    }

    private FloatBuffer buildInputBuffer(List<? extends KeyFrame<?>> keyFrames, int frameRate) {
        var buffer = FloatBuffer.allocate(keyFrames.size());
        for (var keyFrame : keyFrames) {
            buffer.put((float) keyFrame.frame() / (float) frameRate);
        }
        return buffer.flip();
    }

    private <T> FloatBuffer buildOutputBuffer(List<KeyFrame<T>> keyFrames, BiConsumer<T, FloatBuffer> toBuffer) {
        var buffer = FloatBuffer.allocate(keyFrames.size() * 4);
        for (var keyFrame : keyFrames) {
            toBuffer.accept(keyFrame.value(), buffer);
        }
        return buffer.flip();
    }

    private AccessorID buildAccessor(BufferViewID bufferView, int count, AccessorType type, Float min, Float max) {
        var accessor = ImmutableAccessor.builder()
            .bufferView(bufferView)
            .componentType(AccessorComponentType.FLOAT)
            .count(count)
            .type(type)
            .min(Optional.ofNullable(min).map(Scalar::new))
            .max(Optional.ofNullable(max).map(Scalar::new))
            .build();
        return context.addAccessor(accessor);
    }
}
