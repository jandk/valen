package be.twofold.valen.game.eternal.reader.file.mapresources;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public record MapResources(
    List<String> layerNames,
    int unknown,
    List<String> assetTypes,
    List<MapResourcesAsset> assets,
    List<String> mapNames
) {
    public static MapResources read(BinarySource source) throws IOException {
        var numLayerNames = source.order(ByteOrder.BIG_ENDIAN).readInt();
        var layerNames = source.order(ByteOrder.LITTLE_ENDIAN).readStrings(numLayerNames, StringFormat.INT_LENGTH);
        var unknown = source.readInt();

        var numAssetTypes = source.order(ByteOrder.BIG_ENDIAN).readInt();
        var assetTypes = source.order(ByteOrder.LITTLE_ENDIAN).readStrings(numAssetTypes, StringFormat.INT_LENGTH);

        var numAssets = source.order(ByteOrder.BIG_ENDIAN).readInt();
        var assets = source.order(ByteOrder.LITTLE_ENDIAN).readObjects(numAssets, MapResourcesAsset::read);

        var numMapNames = source.order(ByteOrder.BIG_ENDIAN).readInt();
        var mapNames = source.order(ByteOrder.LITTLE_ENDIAN).readStrings(numMapNames, StringFormat.INT_LENGTH);

        return new MapResources(
            layerNames,
            unknown,
            assetTypes,
            assets,
            mapNames
        );
    }
}
