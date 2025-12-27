package be.twofold.valen.format.granite;

import java.util.*;

public record GraniteTexture(
    String name,
    int width,
    int height,
    int x,
    int y,
    List<GraniteTextureLayer> layers
) {
}
