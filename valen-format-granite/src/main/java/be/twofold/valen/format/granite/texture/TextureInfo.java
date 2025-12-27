package be.twofold.valen.format.granite.texture;

import java.util.*;

public record TextureInfo(
    String name,
    int width,
    int height,
    int x,
    int y,
    List<TextureLayerInfo> layers
) {
}
