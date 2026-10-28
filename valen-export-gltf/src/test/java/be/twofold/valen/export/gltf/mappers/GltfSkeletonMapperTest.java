package be.twofold.valen.export.gltf.mappers;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.format.gltf.*;
import be.twofold.valen.format.gltf.model.accessor.*;
import be.twofold.valen.format.gltf.model.animation.*;
import be.twofold.valen.format.gltf.model.buffer.*;
import be.twofold.valen.format.gltf.model.bufferview.*;
import be.twofold.valen.format.gltf.model.camera.*;
import be.twofold.valen.format.gltf.model.extension.*;
import be.twofold.valen.format.gltf.model.image.*;
import be.twofold.valen.format.gltf.model.material.*;
import be.twofold.valen.format.gltf.model.mesh.*;
import be.twofold.valen.format.gltf.model.node.*;
import be.twofold.valen.format.gltf.model.sampler.*;
import be.twofold.valen.format.gltf.model.scene.*;
import be.twofold.valen.format.gltf.model.skin.*;
import be.twofold.valen.format.gltf.model.texture.*;
import org.junit.jupiter.api.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.nio.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class GltfSkeletonMapperTest {

    @Test
    void skinJointsAndInverseBindMatricesShareBoneOrder() throws IOException {
        var skeleton = chainSkeleton();
        var context = new RecordingContext();
        var mapped = new GltfSkeletonMapper(context).mapSkin(skeleton);
        var skin = context.skins.getFirst();

        assertThat(skin.getJoints()).containsExactlyElementsOf(mapped.jointNodeIDs());
        assertThat(mapped.jointNodeIDs()).isNotEqualTo(mapped.jointNodeIDs().stream().sorted().toList());

        assertThat(skin.getInverseBindMatrices()).isPresent();
        assertThat(context.accessors.getFirst().getCount()).isEqualTo(skeleton.bones().size());

        var inverseBindMatrices = (FloatBuffer) context.bufferViews.getFirst();
        for (var bone = 0; bone < skeleton.bones().size(); bone++) {
            assertThat(inverseBindMatrices.get(bone * 16 + 12)).isEqualTo((float) bone);
        }
    }

    private static Skeleton chainSkeleton() {
        var bones = List.of(
            bone("root", -1, 0),
            bone("mid", 0, 1),
            bone("leaf", 1, 2)
        );
        return new Skeleton(bones, Axis.Z);
    }

    private static Bone bone(String name, int parent, int index) {
        return new Bone(
            name,
            parent,
            Quaternion.IDENTITY,
            Vector3.ONE,
            Vector3.ZERO,
            Matrix4.fromTranslation(new Vector3(index, 0.0f, 0.0f))
        );
    }

    /**
     * A {@link GltfContext} that records what the mapper writes; unused operations are unsupported.
     */
    private static final class RecordingContext implements GltfContext {
        final List<NodeSchema> nodes = new ArrayList<>();
        final List<SkinSchema> skins = new ArrayList<>();
        final List<AccessorSchema> accessors = new ArrayList<>();
        final List<Buffer> bufferViews = new ArrayList<>();

        @Override
        public NodeID addNode(NodeSchema node) {
            nodes.add(node);
            return NodeID.of(nodes.size() - 1);
        }

        @Override
        public SkinID addSkin(SkinSchema skin) {
            skins.add(skin);
            return SkinID.of(skins.size() - 1);
        }

        @Override
        public AccessorID addAccessor(AccessorSchema accessor) {
            accessors.add(accessor);
            return AccessorID.of(accessors.size() - 1);
        }

        @Override
        public BufferViewID createBufferView(Buffer buffer, BufferViewTarget target) {
            bufferViews.add(buffer);
            return BufferViewID.of(bufferViews.size() - 1);
        }

        @Override
        public void addExtension(Extension extension) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addAnimation(AnimationSchema animation) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BufferID addBuffer(BufferSchema buffer) {
            throw new UnsupportedOperationException();
        }

        @Override
        public BufferViewID addBufferView(BufferViewSchema bufferView) {
            throw new UnsupportedOperationException();
        }

        @Override
        public CameraID addCamera(CameraSchema camera) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ImageID addImage(ImageSchema image) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MaterialID addMaterial(MaterialSchema material) {
            throw new UnsupportedOperationException();
        }

        @Override
        public MeshID addMesh(MeshSchema mesh) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SamplerID addSampler(SamplerSchema sampler) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SceneID addScene(SceneSchema scene) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TextureID addTexture(TextureSchema texture) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ImageID createImage(ByteBuffer buffer, String name, String filename, ImageMimeType mimeType) {
            throw new UnsupportedOperationException();
        }
    }
}
