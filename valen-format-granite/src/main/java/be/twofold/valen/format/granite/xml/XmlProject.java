package be.twofold.valen.format.granite.xml;

import java.io.*;
import java.util.*;

public record XmlProject(
    String name,
    UUID guid,
    String grBuildVersion,
    String buildProfile,
    XmlBuildConfig buildConfig,
    List<XmlLayerDescription> layerConfig,
    List<XmlAsset> importedAssets
) {
    public static XmlProject load(String rawXml) throws IOException {
        return XmlReader.load(rawXml);
    }
}
