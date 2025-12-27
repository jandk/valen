package be.twofold.valen.format.granite.texture;

import be.twofold.valen.format.granite.gdex.*;
import be.twofold.valen.format.granite.xml.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class TextureMapper {
    public TextureMapper() {
    }

    public List<TextureInfo> mapTextures(GdexStruct metadata) throws IOException {
        var metaTextures = metadata
            .findOne(GdexItemTag.ATLS).asStruct()
            .findOne(GdexItemTag.TXTS).asStruct()
            .find(GdexItemTag.TXTR)
            .map(Gdex::asStruct)
            .toList();

        var rawXml = metadata
            .findOne(GdexItemTag.PROJ).asString();
        var xmlTextureIndex = XmlProject.load(rawXml)
            .importedAssets().stream()
            .collect(Collectors.toUnmodifiableMap(XmlAsset::name, Function.identity()));

        return metaTextures.stream()
            .map(metaTexture -> mapTexture(metaTexture, xmlTextureIndex.get(metaTexture.findOne(GdexItemTag.NAME).asString())))
            .toList();
    }

    private TextureInfo mapTexture(GdexStruct metaTexture, XmlAsset xmlAsset) {
        var name = metaTexture.findOne(GdexItemTag.NAME).asString();
        var width = metaTexture.findOne(GdexItemTag.WDTH).asNumber().intValue();
        var height = metaTexture.findOne(GdexItemTag.HGHT).asNumber().intValue();
        var x = metaTexture.findOne(GdexItemTag.XXXX).asNumber().intValue();
        var y = metaTexture.findOne(GdexItemTag.YYYY).asNumber().intValue();
        var srgb = metaTexture.findOne(GdexItemTag.SRGB).asArray().stream()
            .map(obj -> (int) obj != 0)
            .toList();

        var layers = IntStream.range(0, xmlAsset.layers().size())
            .mapToObj(i -> mapLayer(xmlAsset.layers().get(i), srgb.get(i)))
            .toList();

        return new TextureInfo(name, width, height, x, y, layers);
    }

    private TextureLayerInfo mapLayer(XmlLayer layer, boolean srgb) {
        return layer.textures().size() > 1
            ? mapLayerWithMultipleTextures(layer, srgb)
            : mapLayerWithSingleTexture(layer, srgb);
    }

    private TextureLayerInfo mapLayerWithSingleTexture(XmlLayer layer, boolean srgb) {
        var texture = layer.textures().getFirst();
        var name = texture.src().substring(texture.src().lastIndexOf('\\') + 1);
        var width = texture.width();
        var height = texture.height();
        return new TextureLayerInfo(name, width, height, srgb);
    }

    private TextureLayerInfo mapLayerWithMultipleTextures(XmlLayer layer, boolean srgb) {
        var widths = new HashMap<Integer, Integer>();
        var heights = new HashMap<Integer, Integer>();
        for (var texture : layer.textures()) {
            Check.state(texture.subIndex() == 0, "Can only deal with one subIndex");
            Check.state(texture.arrayIndex() == 0, "Can only deal with one arrayIndex");
            widths.merge(texture.column().orElseThrow(), texture.width(), Math::max);
            heights.merge(texture.row().orElseThrow(), texture.height(), Math::max);
        }
        var texture = layer.textures().getFirst();
        var name = texture.src().substring(texture.src().lastIndexOf('\\') + 1);
        var width = widths.values().stream().mapToInt(Integer::intValue).sum();
        var height = heights.values().stream().mapToInt(Integer::intValue).sum();
        return new TextureLayerInfo(name, width, height, srgb);
    }
}
