package be.twofold.valen.export.gltf;

import be.twofold.valen.export.gltf.model.*;

import java.nio.*;

public interface GltfContext {

    int addAccessor(AccessorSchema accessor);

    int addNode(NodeSchema node);

    int createBufferView(Buffer buffer, int length, BufferViewTarget target);

}
