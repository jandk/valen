package be.twofold.valen.game.eternal.reader.file.mapresources;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.util.*;

public record MapResources(
    List<String> layerNames,
    int unknown,
    List<String> assetTypes,
    List<MapResourcesAsset> assets,
    List<String> mapNames
) {
    public static MapResources read(BinaryReader reader) throws IOException {
        var numLayerNames = reader.readIntBE();
        var layerNames = reader.readObjects(numLayerNames, BinaryReader::readPString);
        var unknown = reader.readInt();

        var numAssetTypes = reader.readIntBE();
        var assetTypes = reader.readObjects(numAssetTypes, BinaryReader::readPString);

        var numAssets = reader.readIntBE();
        var assets = reader.readObjects(numAssets, MapResourcesAsset::read);

        var numMapNames = reader.readIntBE();
        var mapNames = reader.readObjects(numMapNames, BinaryReader::readPString);

        return new MapResources(
            layerNames,
            unknown,
            assetTypes,
            assets,
            mapNames
        );
    }
}
