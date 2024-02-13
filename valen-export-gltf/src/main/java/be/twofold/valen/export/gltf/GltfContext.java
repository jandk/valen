package be.twofold.valen.export.gltf;

import be.twofold.valen.export.gltf.model.*;

import java.nio.*;

public interface GltfContext {

    AccessorId addAccessor(AccessorSchema accessor);

    NodeId addNode(NodeSchema node);

    BufferViewId createBufferView(Buffer buffer, int length, BufferViewTarget target);

}
