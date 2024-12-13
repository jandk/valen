package org.redeye.valen.game.halflife.providers;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;

import java.io.*;

public record WadEntry(
    int offset,
    int size,
    int uncompressedSize,
    WadEntryType type,
    int compression,
    String name
) {

    static WadEntry read(DataSource source) throws IOException {
        var offset = source.readInt();
        var size = source.readInt();
        var uncompressedSize = source.readInt();
        var type = WadEntryType.forValue(source.readByte());
        var compression = source.readByte();
        source.skip(2);
        var name = source.readString(16).trim();
        return new WadEntry(offset, size, uncompressedSize, type, compression, name);
    }

    public AssetType<?> getAssetType() {
        switch (type) {
            case PALETTE, QPIC, MIPTEX, FONT -> {
                return AssetType.TEXTURE;
            }
            default -> {
                return AssetType.BINARY;
            }
        }

    }
}
