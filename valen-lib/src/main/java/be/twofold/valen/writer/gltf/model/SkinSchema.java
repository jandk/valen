package be.twofold.valen.writer.gltf.model;

import java.util.*;

public record SkinSchema(
    int skeleton,
    List<Integer> joints,
    int inverseBindMatrices
) {
}
