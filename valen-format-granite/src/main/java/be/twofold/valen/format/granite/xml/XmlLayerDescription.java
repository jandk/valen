package be.twofold.valen.format.granite.xml;

public record XmlLayerDescription(
    String name,
    String compressionFormat,
    String qualityProfile,
    String dataType,
    String defaultColor
) {
}
