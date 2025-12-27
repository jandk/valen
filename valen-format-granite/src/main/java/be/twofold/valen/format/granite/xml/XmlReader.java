package be.twofold.valen.format.granite.xml;

import javax.xml.stream.*;
import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

final class XmlReader {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss.SSSSSSS a XXX");

    private XmlReader() {
    }

    static XmlProject load(String rawXml) throws IOException {
        try {
            var reader = XMLInputFactory.newInstance()
                .createXMLStreamReader(new StringReader(rawXml));
            return new XmlReader().parseProject(reader);
        } catch (XMLStreamException e) {
            throw new IOException("Could not load xml project", e);
        }
    }

    private XmlProject parseProject(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            if (reader.next() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("Project")) {
                var name = reader.getAttributeValue(null, "Name");
                var guid = UUID.fromString(reader.getAttributeValue(null, "Guid"));
                var grBuildVersion = reader.getAttributeValue(null, "GrBuildVersion");
                var buildProfile = reader.getAttributeValue(null, "BuildProfile");

                var buildConfig = (XmlBuildConfig) null;
                var layerConfig = (List<XmlLayerDescription>) null;
                var importedAssets = (List<XmlAsset>) null;

                while (reader.hasNext()) {
                    var event = reader.next();
                    if (event == XMLStreamConstants.START_ELEMENT) {
                        var elementName = reader.getLocalName();
                        switch (elementName) {
                            case "BuildConfig" -> buildConfig = parseBuildConfig(reader);
                            case "LayerConfig" -> layerConfig = parseLayerConfig(reader);
                            case "ImportedAssets" -> importedAssets = parseImportedAssets(reader);
                        }
                    } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("Project")) {
                        break;
                    }
                }

                return new XmlProject(name, guid, grBuildVersion, buildProfile, buildConfig, layerConfig, importedAssets);
            }
        }
        throw new XMLStreamException("No Project element found");
    }

    private XmlBuildConfig parseBuildConfig(XMLStreamReader reader) throws XMLStreamException {
        var outputDirectory = (String) null;
        var soupOutputDirectory = (String) null;
        var outputType = (String) null;
        var outputName = (String) null;
        var warningLevel = 0;
        var logFile = (String) null;
        var tilingMode = (String) null;
        var maximumAnisotropy = 0;
        var customPageSize = 0;
        var customTargetDisk = (String) null;
        var customBlockSize = 0;
        var customTileWidth = 0;
        var customTileHeight = 0;
        var pagingStrategy = (String) null;

        while (reader.hasNext()) {
            var event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                var text = reader.getElementText();
                switch (reader.getLocalName()) {
                    case "OutputDirectory" -> outputDirectory = text;
                    case "SoupOutputDirectory" -> soupOutputDirectory = text;
                    case "OutputType" -> outputType = text;
                    case "OutputName" -> outputName = text;
                    case "WarningLevel" -> warningLevel = Integer.parseInt(text);
                    case "LogFile" -> logFile = text;
                    case "TilingMode" -> tilingMode = text;
                    case "MaximumAnisotropy" -> maximumAnisotropy = Integer.parseInt(text);
                    case "CustomPageSize" -> customPageSize = Integer.parseInt(text);
                    case "CustomTargetDisk" -> customTargetDisk = text;
                    case "CustomBlockSize" -> customBlockSize = Integer.parseInt(text);
                    case "CustomTileWidth" -> customTileWidth = Integer.parseInt(text);
                    case "CustomTileHeight" -> customTileHeight = Integer.parseInt(text);
                    case "PagingStrategy" -> pagingStrategy = text;
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("BuildConfig")) {
                break;
            }
        }

        return new XmlBuildConfig(
            outputDirectory,
            soupOutputDirectory,
            outputType,
            outputName,
            warningLevel,
            logFile,
            tilingMode,
            maximumAnisotropy,
            customPageSize,
            customTargetDisk,
            customBlockSize,
            customTileWidth,
            customTileHeight,
            pagingStrategy
        );
    }

    private List<XmlLayerDescription> parseLayerConfig(XMLStreamReader reader) throws XMLStreamException {
        var layerDescriptions = new ArrayList<XmlLayerDescription>();

        while (reader.hasNext()) {
            var event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("LayerDescription")) {
                layerDescriptions.add(parseLayerDescription(reader));
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("LayerConfig")) {
                break;
            }
        }

        return layerDescriptions;
    }

    private static XmlLayerDescription parseLayerDescription(XMLStreamReader reader) {
        var name = reader.getAttributeValue(null, "Name");
        var compressionFormat = reader.getAttributeValue(null, "CompressionFormat");
        var qualityProfile = reader.getAttributeValue(null, "QualityProfile");
        var dataType = reader.getAttributeValue(null, "DataType");
        var defaultColor = reader.getAttributeValue(null, "DefaultColor");
        return new XmlLayerDescription(name, compressionFormat, qualityProfile, dataType, defaultColor);
    }

    private List<XmlAsset> parseImportedAssets(XMLStreamReader reader) throws XMLStreamException {
        var assets = new ArrayList<XmlAsset>();
        while (reader.hasNext()) {
            var event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("Asset")) {
                assets.add(parseAsset(reader));
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("ImportedAssets")) {
                break;
            }
        }
        return assets;
    }

    private XmlAsset parseAsset(XMLStreamReader reader) throws XMLStreamException {
        var name = reader.getAttributeValue(null, "Name");
        var guid = UUID.fromString(reader.getAttributeValue(null, "GUID").substring(1, 37));
        var width = Integer.parseInt(reader.getAttributeValue(null, "Width"));
        var height = Integer.parseInt(reader.getAttributeValue(null, "Height"));
        var targetWidth = Integer.parseInt(reader.getAttributeValue(null, "TargetWidth"));
        var targetHeight = Integer.parseInt(reader.getAttributeValue(null, "TargetHeight"));
        var autoScalingMode = reader.getAttributeValue(null, "AutoScalingMode");
        var tilingMethod = reader.getAttributeValue(null, "TilingMethod");
        var type = reader.getAttributeValue(null, "Type");

        var layers = new ArrayList<XmlLayer>();
        while (reader.hasNext()) {
            var event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("Layer")) {
                layers.add(parseLayer(reader));
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("Asset")) {
                break;
            }
        }

        return new XmlAsset(name, guid, width, height, targetWidth, targetHeight, autoScalingMode, tilingMethod, type, layers);
    }

    private XmlLayer parseLayer(XMLStreamReader reader) throws XMLStreamException {
        var qualityProfile = reader.getAttributeValue(null, "QualityProfile");
        var flip = reader.getAttributeValue(null, "Flip");
        var targetWidth = Integer.parseInt(reader.getAttributeValue(null, "TargetWidth"));
        var targetHeight = Integer.parseInt(reader.getAttributeValue(null, "TargetHeight"));
        var resizeMode = reader.getAttributeValue(null, "ResizeMode");
        var mipSource = reader.getAttributeValue(null, "MipSource");
        var textureType = reader.getAttributeValue(null, "TextureType");
        var assetPackingMode = reader.getAttributeValue(null, "AssetPackingMode");

        var textures = new ArrayList<XmlTexture>();
        while (reader.hasNext()) {
            var event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT && reader.getLocalName().equals("Texture")) {
                textures.add(parseTexture(reader));
            } else if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("Layer")) {
                break;
            }
        }

        return new XmlLayer(qualityProfile, flip, targetWidth, targetHeight, resizeMode, mipSource, textureType, assetPackingMode, textures);
    }

    private XmlTexture parseTexture(XMLStreamReader reader) throws XMLStreamException {
        var src = reader.getAttributeValue(null, "Src");
        var row = Optional.ofNullable(reader.getAttributeValue(null, "Row")).map(Integer::parseInt);
        var column = Optional.ofNullable(reader.getAttributeValue(null, "Column")).map(Integer::parseInt);
        var subIndex = Integer.parseInt(reader.getAttributeValue(null, "SubIndex"));
        var width = Integer.parseInt(reader.getAttributeValue(null, "Width"));
        var height = Integer.parseInt(reader.getAttributeValue(null, "Height"));
        var arrayIndex = Integer.parseInt(reader.getAttributeValue(null, "ArrayIndex"));
        var lastChangeDate = OffsetDateTime.parse(reader.getAttributeValue(null, "LastChangeDate"), FORMATTER).toInstant();
        var numChannels = Integer.parseInt(reader.getAttributeValue(null, "NumChannels"));

        while (reader.hasNext()) {
            var event = reader.next();
            if (event == XMLStreamConstants.END_ELEMENT && reader.getLocalName().equals("Texture")) {
                break;
            }
        }

        return new XmlTexture(src, row, column, subIndex, width, height, arrayIndex, lastChangeDate, numChannels);
    }
}
