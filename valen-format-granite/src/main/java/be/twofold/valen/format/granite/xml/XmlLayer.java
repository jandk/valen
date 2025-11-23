package be.twofold.valen.format.granite.xml;

import java.util.*;

public record XmlLayer(
    String qualityProfile,
    String flip,
    int targetWidth,
    int targetHeight,
    String resizeMode,
    String mipSource,
    String textureType,
    String assetPackingMode,
    List<XmlTexture> textures
) {
}
