package be.twofold.valen.writer.gltf.model;

import java.util.*;

public record SceneSchema(
    List<Integer> nodes
) {
    public SceneSchema {
        // nodes = List.copyOf(nodes);
    }
}
