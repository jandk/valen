package be.twofold.valen.format.granite.xml;

public record XmlBuildConfig(
    String outputDirectory,
    String soupOutputDirectory,
    String outputType,
    String outputName,
    int warningLevel,
    String logFile,
    String tilingMode,
    int maximumAnisotropy,
    int customPageSize,
    String customTargetDisk,
    int customBlockSize,
    int customTileWidth,
    int customTileHeight,
    String pagingStrategy
) {
}
