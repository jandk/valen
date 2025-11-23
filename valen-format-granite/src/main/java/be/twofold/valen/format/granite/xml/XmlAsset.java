package be.twofold.valen.format.granite.xml;

import java.util.*;

public record XmlAsset(
    String name,
    UUID guid,
    int width,
    int height,
    int targetWidth,
    int targetHeight,
    String autoScalingMode,
    String tilingMethod,
    String type,
    List<XmlLayer> layers
) {
}
