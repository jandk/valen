package be.twofold.valen.reader.file.mapresources;

import be.twofold.valen.core.util.*;

import java.util.*;

public record MapResources(
    List<String> layerNames,
    int unknown,
    List<String> assetTypes,
    List<MapResourcesAsset> assets,
    List<String> mapNames
) {
    public static MapResources read(BetterBuffer buffer) {
        var numLayerNames = Integer.reverseBytes(buffer.getInt());
        var layerNames = buffer.getStructs(numLayerNames, BetterBuffer::getString);
        var unknown = buffer.getInt();

        var numAssetTypes = Integer.reverseBytes(buffer.getInt());
        var assetTypes = buffer.getStructs(numAssetTypes, BetterBuffer::getString);

        var numAssets = Integer.reverseBytes(buffer.getInt());
        var assets = buffer.getStructs(numAssets, MapResourcesAsset::read);

        var numMapNames = Integer.reverseBytes(buffer.getInt());
        var mapNames = buffer.getStructs(numMapNames, BetterBuffer::getString);

        return new MapResources(
            layerNames,
            unknown,
            assetTypes,
            assets,
            mapNames
        );
    }
}
