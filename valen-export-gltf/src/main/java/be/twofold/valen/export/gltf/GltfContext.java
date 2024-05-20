package be.twofold.valen.export.gltf;

import be.twofold.valen.export.gltf.model.*;

import java.nio.*;
import java.util.*;

public interface GltfContext {

    AccessorId addAccessor(AccessorSchema accessor);

    NodeId addNode(NodeSchema node);

    BufferViewId createBufferView(Buffer buffer, int length, BufferViewTarget target);

    TextureId allocateTextureId(String materialName);

    List<String> getAllocatedTextures();

    MaterialId addMaterial(MaterialSchema material);

    MaterialId findMaterial(String materialName);
}
