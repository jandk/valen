package be.twofold.valen;

import be.twofold.valen.core.util.*;

import java.util.*;

public record MapResources(
    int magic,
    List<String> layerNames,
    int unknown,
    List<String> assetTypes,
    List<MapResourcesAsset> assets,
    List<String> mapNames
) {
    private static MapResources read(BetterBuffer buffer) {
        var magic = buffer.getInt();
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
            magic,
            layerNames,
            unknown,
            assetTypes,
            assets,
            mapNames
        );
    }
}
