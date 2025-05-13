package be.twofold.valen.game.darkages;

import be.twofold.valen.game.darkages.reader.resources.*;

public record DarkAgesResource(
    ResourceName name,
    ResourcesType type,
    ResourcesVariation variation,
    int offset,
    int compressedSize,
    int size,
    ResourcesCompressionMode compression,
    long hash,
    long checksum,
    int version
) {
    static DarkAgesResource from(DarkAgesAsset asset) {
        return new DarkAgesResource(
            asset.id().name(),
            asset.id().type(),
            asset.id().variation(),
            asset.offset(),
            asset.compressedSize(),
            asset.size(),
            asset.compression(),
            asset.hash(),
            asset.checksum(),
            asset.version()
        );
    }
}
