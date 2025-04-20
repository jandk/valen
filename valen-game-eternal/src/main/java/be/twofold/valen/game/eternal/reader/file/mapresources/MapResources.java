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
        var numLayerNames = source.readIntBE();
        var layerNames = source.readObjects(numLayerNames, DataSource::readPString);
        var unknown = source.readInt();

        var numAssetTypes = source.readIntBE();
        var assetTypes = source.readObjects(numAssetTypes, DataSource::readPString);

        var numAssets = source.readIntBE();
        var assets = source.readObjects(numAssets, MapResourcesAsset::read);

        var numMapNames = source.readIntBE();
        var mapNames = source.readObjects(numMapNames, DataSource::readPString);

        return new MapResources(
            layerNames,
            unknown,
            assetTypes,
            assets,
            mapNames
        );
    }
}
