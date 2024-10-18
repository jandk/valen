package be.twofold.valen.core.scene;

import java.util.*;

public record Scene(
    List<Instance> instances
) {
    public Scene {
        instances = List.copyOf(instances);
    }
}
