package be.twofold.valen.format.granite.xml;

import org.w3c.dom.*;
import org.w3c.dom.Node;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.stream.*;

final class XmlReader {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss.SSSSSSS a XXX");

    private XmlReader() {
    }

    static XmlProject load(String rawXml) throws IOException {
        try {
            var document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(new InputSource(new StringReader(rawXml)));
            return new XmlReader().parseProject(document.getDocumentElement());
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException("Could not load xml project", e);
        }
    }

    private XmlProject parseProject(Element element) {
        var attrs = element.getAttributes();
        var name = attrs.getNamedItem("Name").getNodeValue();
        var guid = UUID.fromString(attrs.getNamedItem("Guid").getNodeValue());
        var grBuildVersion = attrs.getNamedItem("GrBuildVersion").getNodeValue();
        var buildProfile = attrs.getNamedItem("BuildProfile").getNodeValue();
        var buildConfig = parseBuildConfig(findChild(element, "BuildConfig"));
        var layerConfigElement = findChild(element, "LayerConfig");
        var layerConfig = findChildren(layerConfigElement, "LayerDescription")
            .map(this::parseLayerDescription)
            .toList();
        var importedAssetsElement = findChild(element, "ImportedAssets");
        var importedAssets = findChildren(importedAssetsElement, "Asset")
            .map(this::parseAsset)
            .toList();

        return new XmlProject(
            name,
            guid,
            grBuildVersion,
            buildProfile,
            buildConfig,
            layerConfig,
            importedAssets
        );
    }

    private XmlBuildConfig parseBuildConfig(Element element) {
        var outputDirectory = Path.of(findChild(element, "OutputDirectory").getTextContent());
        var soupOutputDirectory = Path.of(findChild(element, "SoupOutputDirectory").getTextContent());
        var outputType = findChild(element, "OutputType").getTextContent();
        var outputName = findChild(element, "OutputName").getTextContent();
        var warningLevel = Integer.parseInt(findChild(element, "WarningLevel").getTextContent());
        var logFile = findChild(element, "LogFile").getTextContent();
        var tilingMode = findChild(element, "TilingMode").getTextContent();
        var maximumAnisotropy = Integer.parseInt(findChild(element, "MaximumAnisotropy").getTextContent());
        var customPageSize = Integer.parseInt(findChild(element, "CustomPageSize").getTextContent());
        var customTargetDisk = findChild(element, "CustomTargetDisk").getTextContent();
        var customBlockSize = Integer.parseInt(findChild(element, "CustomBlockSize").getTextContent());
        var customTileWidth = Integer.parseInt(findChild(element, "CustomTileWidth").getTextContent());
        var customTileHeight = Integer.parseInt(findChild(element, "CustomTileHeight").getTextContent());
        var pagingStrategy = findChild(element, "PagingStrategy").getTextContent();

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

    private XmlLayerDescription parseLayerDescription(Element element) {
        var attrs = element.getAttributes();
        var name = attrs.getNamedItem("Name").getNodeValue();
        var compressionFormat = attrs.getNamedItem("CompressionFormat").getNodeValue();
        var qualityProfile = attrs.getNamedItem("QualityProfile").getNodeValue();
        var dataType = attrs.getNamedItem("DataType").getNodeValue();
        var defaultColor = attrs.getNamedItem("DefaultColor").getNodeValue();

        return new XmlLayerDescription(
            name,
            compressionFormat,
            qualityProfile,
            dataType,
            defaultColor
        );
    }

    private XmlAsset parseAsset(Element element) {
        var attrs = element.getAttributes();
        var name = attrs.getNamedItem("Name").getNodeValue();
        var guid = UUID.fromString(attrs.getNamedItem("GUID").getNodeValue().substring(1, 37));
        var width = Integer.parseInt(attrs.getNamedItem("Width").getNodeValue());
        var height = Integer.parseInt(attrs.getNamedItem("Height").getNodeValue());
        var targetWidth = Integer.parseInt(attrs.getNamedItem("TargetWidth").getNodeValue());
        var targetHeight = Integer.parseInt(attrs.getNamedItem("TargetHeight").getNodeValue());
        var autoScalingMode = attrs.getNamedItem("AutoScalingMode").getNodeValue();
        var tilingMethod = attrs.getNamedItem("TilingMethod").getNodeValue();
        var type = attrs.getNamedItem("Type").getNodeValue();

        var layersNode = findChild(element, "Layers");
        var layers = findChildren(layersNode)
            .map(this::parseLayer)
            .toList();

        return new XmlAsset(
            name,
            guid,
            width,
            height,
            targetWidth,
            targetHeight,
            autoScalingMode,
            tilingMethod,
            type,
            layers
        );
    }

    private XmlLayer parseLayer(Element element) {
        var attrs = element.getAttributes();
        var qualityProfile = attrs.getNamedItem("QualityProfile").getNodeValue();
        var flip = attrs.getNamedItem("Flip").getNodeValue();
        var targetWidth = Integer.parseInt(attrs.getNamedItem("TargetWidth").getNodeValue());
        var targetHeight = Integer.parseInt(attrs.getNamedItem("TargetHeight").getNodeValue());
        var resizeMode = attrs.getNamedItem("ResizeMode").getNodeValue();
        var mipSource = attrs.getNamedItem("MipSource").getNodeValue();
        var textureType = attrs.getNamedItem("TextureType").getNodeValue();
        var assetPackingMode = attrs.getNamedItem("AssetPackingMode").getNodeValue();

        var texturesNode = findChild(element, "Textures");
        var textures = findChildren(texturesNode)
            .map(this::parseTexture)
            .toList();

        return new XmlLayer(
            qualityProfile,
            flip,
            targetWidth,
            targetHeight,
            resizeMode,
            mipSource,
            textureType,
            assetPackingMode,
            textures
        );
    }

    private XmlTexture parseTexture(Element element) {
        var attrs = element.getAttributes();
        var src = Path.of(attrs.getNamedItem("Src").getNodeValue());
        var subIndex = Integer.parseInt(attrs.getNamedItem("SubIndex").getNodeValue());
        var width = Integer.parseInt(attrs.getNamedItem("Width").getNodeValue());
        var height = Integer.parseInt(attrs.getNamedItem("Height").getNodeValue());
        var arrayIndex = Integer.parseInt(attrs.getNamedItem("ArrayIndex").getNodeValue());
        var lastChangeDate = OffsetDateTime.parse(attrs.getNamedItem("LastChangeDate").getNodeValue(), FORMATTER).toInstant();
        var numChannels = Integer.parseInt(attrs.getNamedItem("NumChannels").getNodeValue());

        return new XmlTexture(
            src,
            subIndex,
            width,
            height,
            arrayIndex,
            lastChangeDate,
            numChannels
        );
    }

    private Stream<Element> findChildren(Node node) {
        var childNodes = node.getChildNodes();
        return IntStream.range(0, childNodes.getLength())
            .mapToObj(childNodes::item)
            .filter(n -> n.getNodeType() == Node.ELEMENT_NODE)
            .map(n -> (Element) n);
    }

    private Stream<Element> findChildren(Node node, String name) {
        return findChildren(node)
            .filter(n -> n.getNodeName().equals(name));
    }

    private Element findChild(Node node, String name) {
        var iterator = findChildren(node, name).iterator();
        var result = iterator.next();
        if (iterator.hasNext()) {
            throw new IllegalStateException("Expected one of " + name);
        }
        return result;
    }
}
