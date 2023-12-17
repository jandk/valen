package be.twofold.valen.export.gltf.model;

import java.util.*;

public record SkinSchema(
    int skeleton,
    List<Integer> joints,
    int inverseBindMatrices
) {
}
