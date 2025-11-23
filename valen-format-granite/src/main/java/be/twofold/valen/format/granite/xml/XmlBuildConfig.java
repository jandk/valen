package be.twofold.valen.format.granite.xml;

import java.nio.file.*;

public record XmlBuildConfig(
    Path outputDirectory,
    Path soupOutputDirectory,
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
