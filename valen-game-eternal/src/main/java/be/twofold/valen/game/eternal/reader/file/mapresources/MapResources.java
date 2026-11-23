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
    public static MapResources read(BinaryReader reader) throws IOException {
        var numLayerNames = reader.order(ByteOrder.BIG_ENDIAN).readInt();
        var layerNames = reader.order(ByteOrder.LITTLE_ENDIAN).readStrings(numLayerNames, StringFormat.INT_LENGTH);
        var unknown = reader.readInt();

        var numAssetTypes = reader.order(ByteOrder.BIG_ENDIAN).readInt();
        var assetTypes = reader.order(ByteOrder.LITTLE_ENDIAN).readStrings(numAssetTypes, StringFormat.INT_LENGTH);

        var numAssets = reader.order(ByteOrder.BIG_ENDIAN).readInt();
        var assets = reader.order(ByteOrder.LITTLE_ENDIAN).readObjects(numAssets, MapResourcesAsset::read);

        var numMapNames = reader.order(ByteOrder.BIG_ENDIAN).readInt();
        var mapNames = reader.order(ByteOrder.LITTLE_ENDIAN).readStrings(numMapNames, StringFormat.INT_LENGTH);

        return new MapResources(
            layerNames,
            unknown,
            assetTypes,
            assets,
            mapNames
        );
    }
}
