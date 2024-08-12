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
    public static MapResources read(DataSource source) throws IOException {
        var numLayerNames = Integer.reverseBytes(source.readInt());
        var layerNames = source.readStructs(numLayerNames, DataSource::readPString);
        var unknown = source.readInt();

        var numAssetTypes = Integer.reverseBytes(source.readInt());
        var assetTypes = source.readStructs(numAssetTypes, DataSource::readPString);

        var numAssets = Integer.reverseBytes(source.readInt());
        var assets = source.readStructs(numAssets, MapResourcesAsset::read);

        var numMapNames = Integer.reverseBytes(source.readInt());
        var mapNames = source.readStructs(numMapNames, DataSource::readPString);

        return new MapResources(
            layerNames,
            unknown,
            assetTypes,
            assets,
            mapNames
        );
    }
}
