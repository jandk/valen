package be.twofold.valen.gltf;

import be.twofold.valen.gltf.model.accessor.*;
import be.twofold.valen.gltf.model.animation.*;
import be.twofold.valen.gltf.model.buffer.*;
import be.twofold.valen.gltf.model.bufferview.*;
import be.twofold.valen.gltf.model.camera.*;
import be.twofold.valen.gltf.model.extension.*;
import be.twofold.valen.gltf.model.image.*;
import be.twofold.valen.gltf.model.material.*;
import be.twofold.valen.gltf.model.mesh.*;
import be.twofold.valen.gltf.model.node.*;
import be.twofold.valen.gltf.model.sampler.*;
import be.twofold.valen.gltf.model.scene.*;
import be.twofold.valen.gltf.model.skin.*;
import be.twofold.valen.gltf.model.texture.*;

import java.io.*;
import java.nio.*;

public interface GltfContext {

    void addExtension(Extension extension);

    AccessorID addAccessor(AccessorSchema accessor);

    void addAnimation(AnimationSchema animation);

    BufferID addBuffer(BufferSchema buffer);

    BufferViewID addBufferView(BufferViewSchema bufferView);

    CameraID addCamera(CameraSchema camera);

    ImageID addImage(ImageSchema image);

    MaterialID addMaterial(MaterialSchema material);

    MeshID addMesh(MeshSchema mesh);

    NodeID addNode(NodeSchema node);

    SamplerID addSampler(SamplerSchema sampler);

    SceneID addScene(SceneSchema scene);

    SkinID addSkin(SkinSchema skin);

    TextureID addTexture(TextureSchema texture);

    BufferViewID createBufferView(Buffer buffer, BufferViewTarget target) throws IOException;

    ImageID createImage(ByteBuffer buffer, String name, String filename, ImageMimeType mimeType) throws IOException;

    NodeID nextNodeId();

}
